package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.utils.CommonUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.finance.bo.wx.PrePayInfo;
import com.yoogurt.taxi.finance.bo.wx.PrePayResult;
import com.yoogurt.taxi.finance.dao.WxSettingsDao;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.AbstractFinanceBizService;
import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.finance.task.PayTask;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WxPayServiceImpl extends AbstractFinanceBizService implements WxPayService {

    private static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    @Autowired
    private WxSettingsDao wxSettingsDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public FinanceWxSettings addWxSettings(FinanceWxSettings settings) {
        if (settings == null) return null;
        if (wxSettingsDao.insertSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceWxSettings updateWxSettings(FinanceWxSettings settings) {
        if (settings == null) return null;
        if (wxSettingsDao.updateByIdSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceWxSettings getWxSettings(String appId) {
        if (StringUtils.isBlank(appId)) return null;
        return wxSettingsDao.selectById(appId);
    }

    @Override
    public CompletableFuture<ResponseObj> doTask(final PayTask payTask) {
        if (payTask == null) return null;
        final PayForm payParams = payTask.getPayParams();
        if(payParams == null) return null;
        final String appId = payParams.getAppId();
        if(StringUtils.isBlank(appId)) return null;
        return CompletableFuture.supplyAsync(() -> {
            try {
                final FinanceWxSettings settings = getWxSettings(appId);
                if (settings == null) return ResponseObj.fail(StatusCode.BIZ_FAILED, "该应用暂不支持微信支付");
                final PrePayInfo pay = buildPrePayInfo(settings, payTask.getPayParams());
                final Document document = BeanRefUtils.toXml(pay, "xml");
                final ResponseEntity<String> prepayResult = restTemplate.postForEntity(URL_UNIFIED_ORDER, document.asXML(), String.class);
                //请求正常
                if (prepayResult.getStatusCode().equals(HttpStatus.OK)) {
                    //控制台输出请求结果
                    CommonUtils.xmlOutput(DocumentHelper.parseText(prepayResult.getBody()));
                    //转换成JSON格式
                    XMLSerializer serializer = new XMLSerializer();
                    final JSONObject jsonObject = (JSONObject) serializer.read(prepayResult.getBody().replaceAll("^<\\?.*", "").replaceAll("\\r|\\t|\\n|\\s", ""));
                    if(!jsonObject.optString("return_code").equals("SUCCESS")){	//预下单不成功
                        //1、判断 return_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "获取支付对象失败");
                    }else if(!jsonObject.optString("result_code").equals("SUCCESS")){
                        //2、判断 result_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "下单失败");
                    }else{	//预下单请求成功
                        PrePayResult prePayResult = PrePayResult.builder()
                                .appId(jsonObject.optString("appid"))
                                .prepayId(jsonObject.optString("prepay_id"))
                                .nonceStr(jsonObject.optString("nonce_str"))
                                .merchantId(jsonObject.optString("mch_id"))
                                .codeUrl(jsonObject.optString("code_url"))
                                .sign(jsonObject.optString("sign"))
                                .tradeType(jsonObject.optString("trade_type"))
                                .resultCode(jsonObject.optString("result_code"))
                                .returnCode(jsonObject.optString("return_code"))
                                .returnMsg(jsonObject.optString("return_msg"))
                                .errCode(jsonObject.optString("err_code"))
                                .errCodeDes(jsonObject.optString("err_code_des"))
                                .build();
                        return ResponseObj.success(prePayResult);
                    }
                }
            } catch (DocumentException e) {
                log.error("微信预下单请求发生异常, {}", e);
            }
            return ResponseObj.fail(StatusCode.SYS_ERROR, StatusCode.SYS_ERROR.getDetail());
        });
    }

    /**
     * 构造一个预下单对象
     * @param settings 微信支付配置项
     * @param payParams 客户端提交的支付请求参数
     * @return 预下单对象
     */
    private PrePayInfo buildPrePayInfo(FinanceWxSettings settings, PayForm payParams) {
        Map<String, Object> extras = payParams.getExtras();
        PrePayInfo pay = PrePayInfo.builder()
                .appid(settings.getAppId())
                .nonce_str(UUID.randomUUID().toString())
                .notify_url(super.getNotifyUrl()) //通知url必须为直接可访问的url，不能携带参数
                .mch_id(settings.getMerchantId())
                .body(payParams.getBody())
                .out_trade_no(payParams.getOrderNo())
                .total_fee(payParams.getAmount())
                .spbill_create_ip(payParams.getClientIp())
                .trade_type((extras != null && extras.get("trade_type") != null) ? extras.get("trade_type").toString() : "APP")
                .appid(settings.getWxAppId())
                .key(settings.getPrivateKey())
                .sign_type("MD5")
                .build();
        SortedMap<String, Object> parameters = BeanRefUtils.toSortedMap(pay);
        String sign = createSign(parameters, settings.getPrivateKey());
        pay.setSign(sign);
        return pay;
    }

    /**
     * 生成微信支付签名
     * @param parameters 支付参数
     * @param secretKey 密钥
     * @return 支付签名
     */
    private String createSign(SortedMap<String, Object> parameters, String secretKey){
        String sign = StringUtils.EMPTY;
        try {
            StringBuilder str = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null && !StringUtils.isBlank(value.toString()) && !"sign".equals(key) && !"key".equals(key)) {
                    str.append(key.trim()).append("=").append(value).append("&");
                }
            }
            str.append("key=").append(secretKey.trim()); 	//拼接支付密钥
            sign = DigestUtils.md5Hex(str.toString().getBytes("UTF-8")).toUpperCase();
            log.info("sign = " + sign);
        } catch (UnsupportedEncodingException e) {
            log.error("生成微信支付签名异常, {}", e);
        }
        return sign;
    }
}
