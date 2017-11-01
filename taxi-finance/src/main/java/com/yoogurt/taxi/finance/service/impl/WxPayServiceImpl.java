package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.utils.CommonUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.bo.wx.PrePayInfo;
import com.yoogurt.taxi.finance.bo.wx.PrePayResult;
import com.yoogurt.taxi.finance.dao.WxSettingsDao;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.AbstractFinanceBizService;
import com.yoogurt.taxi.finance.service.PayService;
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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WxPayServiceImpl extends AbstractFinanceBizService implements WxPayService {

    private static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    @Autowired
    private PayService payService;

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
        if (payParams == null) return null;
        final String appId = payParams.getAppId();
        if (StringUtils.isBlank(appId)) return null;
        return CompletableFuture.supplyAsync(() -> {
            try {
                final FinanceWxSettings settings = getWxSettings(appId);
                if (settings == null) return ResponseObj.fail(StatusCode.BIZ_FAILED, "该应用暂不支持微信支付");
                final PrePayInfo pay = buildPrePayInfo(settings, payTask.getPayParams());
                final Document document = BeanRefUtils.toXml(pay, "xml", pay.parameterMap(), "key");
                //控制台输出请求参数
                CommonUtils.xmlOutput(document);
                final ResponseEntity<String> prepayResult = restTemplate.postForEntity(URL_UNIFIED_ORDER, document.asXML(), String.class);
                log.info(prepayResult.getBody());
                //请求正常
                if (prepayResult.getStatusCode().equals(HttpStatus.OK)) {
                    //控制台输出请求结果
                    CommonUtils.xmlOutput(DocumentHelper.parseText(prepayResult.getBody()));
                    //转换成JSON格式
                    XMLSerializer serializer = new XMLSerializer();
                    final JSONObject jsonObject = (JSONObject) serializer.read(prepayResult.getBody().replaceAll("^<\\?.*", "").replaceAll("\\r|\\t|\\n|\\s", ""));
                    if (!jsonObject.optString("return_code").equals("SUCCESS")) {
                        //1、判断 return_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "获取支付对象失败");
                    } else if (!jsonObject.optString("result_code").equals("SUCCESS")) {
                        //2、判断 result_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "下单失败");
                    } else {    //预下单请求成功
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
                        Payment payment = payService.buildPayment(payParams);
                        payment.setCredential(BeanRefUtils.toMap(prePayResult));
                        return ResponseObj.success(payment);
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
     *
     * @param settings  微信支付配置项
     * @param payParams 客户端提交的支付请求参数
     * @return 预下单对象
     */
    private PrePayInfo buildPrePayInfo(FinanceWxSettings settings, PayForm payParams) {
        Map<String, Object> extras = payParams.getExtras();
        DateTime now = DateTime.now();
        PrePayInfo pay = PrePayInfo.builder()
                .appId(settings.getWxAppId())
                .nonceStr(UUID.randomUUID().toString().replaceAll("-", ""))
                .notifyUrl(super.getNotifyUrl()) //通知url必须为直接可访问的url，不能携带参数
                .mchId(settings.getMerchantId())
                .body(payParams.getBody())
                .outTradeNo(payParams.getOrderNo())
                .totalFee(payParams.getAmount())
                .spbillCreateIp(payParams.getClientIp())
                .tradeType((extras != null && extras.get("trade_type") != null) ? extras.get("trade_type").toString() : "APP")
                .key(settings.getApiSecret())
                .signType("MD5")
                .timeStart(now.toString("yyyyMMddHHmmss"))
                .timeExpire(now.plusMinutes(5).toString("yyyyMMddHHmmss"))
                .build();
        SortedMap<String, Object> parameters = BeanRefUtils.toSortedMap(pay, "key");
        String sign = sign(parameters, pay.parameterMap(), "MD5", settings.getApiSecret(), super.getCharset(), "sign");
        pay.setSign(sign);
        return pay;
    }


    /**
     * 参数签名接口
     *
     * @param parameters   参数组装的SortedMap
     * @param parameterMap 字段对应的请求参数，传入null，或者字段名对应的value为null，则以字段名为准
     * @param signType     加密方式，MD5，RSA，RSA2等
     * @param privateKey   加密的私钥
     * @param charset      编码方式
     * @param skipAttrs    从parameters中跳过的属性
     * @return 签名
     */
    @Override
    public String sign(SortedMap<String, Object> parameters, Map<String, Object> parameterMap, String signType, String privateKey, String charset, String... skipAttrs) {

        String sign;
        String content = super.parameterAssemble(parameters, parameterMap, skipAttrs) + "&key=" + privateKey.trim();
        log.info(content);
        sign = DigestUtils.md5Hex(content).toUpperCase();    //拼接支付密钥
        log.info("签名结果：" + sign);
        return sign;
    }
}
