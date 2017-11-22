package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.utils.XmlUtil;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.dal.bo.BaseNotify;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.enums.EventType;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.finance.bo.wx.PrePayInfo;
import com.yoogurt.taxi.finance.bo.wx.PrePayResult;
import com.yoogurt.taxi.finance.dao.WxSettingsDao;
import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.pay.doc.Event;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.doc.Payment;
import com.yoogurt.taxi.pay.params.PayParams;
import com.yoogurt.taxi.pay.service.PayService;
import com.yoogurt.taxi.pay.service.impl.AbstractFinanceBizService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("wxPayService")
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
        if (settings == null) {
            return null;
        }
        if (wxSettingsDao.insertSelective(settings) == 1) {
            return settings;
        }
        return null;
    }

    @Override
    public FinanceWxSettings updateWxSettings(FinanceWxSettings settings) {
        if (settings == null) {
            return null;
        }
        if (wxSettingsDao.updateByIdSelective(settings) == 1) {
            return settings;
        }
        return null;
    }

    @Override
    public FinanceWxSettings getWxSettings(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        return wxSettingsDao.selectById(appId);
    }

    @Override
    public FinanceWxSettings getWxSettingsByAppId(String wxAppId) {
        if (StringUtils.isBlank(wxAppId)) {
            return null;
        }
        FinanceWxSettings settings = new FinanceWxSettings();
        settings.setWxAppId(wxAppId);
        return wxSettingsDao.selectOne(settings);
    }

    @Override
    public CompletableFuture<ResponseObj> doTask(final PayTask payTask) {
        if (payTask == null) {
            return null;
        }
        final PayParams payParams = payTask.getPayParams();
        if (payParams == null) {
            return null;
        }
        final String appId = payParams.getAppId();
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        final FinanceWxSettings settings = getWxSettings(appId);
        if (settings == null) {
            return null;
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                Payment payment = payService.buildPayment(payParams);
                final PrePayInfo pay = buildPrePayInfo(settings, payTask.getPayParams(), payment);
                if (pay == null) {
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "微信预下单失败");
                }
                final Document document = BeanRefUtils.toXml(pay, "xml", pay.parameterMap(), "key");
                //控制台输出请求参数
                log.info(document.asXML());
                final ResponseEntity<String> prepayResult = restTemplate.postForEntity(URL_UNIFIED_ORDER, document.asXML(), String.class);
                //控制台输出请求结果
                log.info("微信预下单请求响应：\n" + prepayResult.getBody());
                //请求正常
                if (prepayResult.getStatusCode().equals(HttpStatus.OK)) {
                    //转换成JSON格式
                    XMLSerializer serializer = new XMLSerializer();
                    final JSONObject jsonObject = (JSONObject) serializer.read(prepayResult.getBody().replaceAll("^<\\?.*", "").replaceAll("\\r|\\t|\\n|\\s", ""));
                    if (!"SUCCESS".equals(jsonObject.optString("return_code"))) {
                        //1、判断 return_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, jsonObject.optString("return_msg"));
                    } else if (!"SUCCESS".equals(jsonObject.optString("result_code"))) {
                        //2、判断 result_code
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "[" + jsonObject.optString("err_code") + "]" + jsonObject.optString("err_code_des"));
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
                                .timestamp(System.currentTimeMillis() / 1000)
                                .build();
                        payment.setCredential(BeanRefUtils.toMap(prePayResult));
                        return ResponseObj.success(payment);
                    }
                }
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "[" + prepayResult.getStatusCode() + ": " + prepayResult.getStatusCodeValue() + "微信预下单请求失败");
            } catch (Exception e) {
                log.error("微信预下单请求发生异常, {}", e);
                return ResponseObj.fail(StatusCode.SYS_ERROR, e.toString());
            }
        });
    }

    /**
     * 解析回调请求，组装EventTask
     *
     * @param parameterMap 回调请求
     * @return EventTask
     */
    @Override
    public Event<? extends BaseNotify> eventParse(Map<String, Object> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return null;
        }
        //生成一个eventId
        String eventId = "event_" + RandomUtils.getPrimaryKey();
        //构造一个 Notify
        WxNotify notify = new WxNotify();
        //构造一个 Event
        Event<WxNotify> event = new Event<>(eventId, notify);
        try {
            //将Map中的参数注入到
            BeanUtils.populate(notify, parameterMap);
            notify.setChannel(PayChannel.WX.getName());
            notify.setCharset("UTF-8");
            notify.setSignType("MD5");
            notify.setSign(parameterMap.get("sign").toString());
            notify.setAmount(Long.valueOf(parameterMap.get("total_fee").toString()));
            long timestamp = DateTime.parse(notify.getTimeEnd(), DateTimeFormat.forPattern("yyyyMMddHHmmss")).getMillis();
            notify.setNotifyTimestamp(timestamp);
            notify.setPaidTimestamp(timestamp);
            //回传参数
            if (parameterMap.get("attach") != null) {
                Map<String, Object> metadata = new HashMap<>(16);
                String orderNo = "";
                String attach = parameterMap.get("attach").toString();
                String[] extras = attach.split("&");
                for (String str : extras) {
                    String[] pairs = str.split("=");
                    if(pairs.length != 2) {
                        continue;
                    }
                    metadata.put(pairs[0], pairs[1]);
                    //解析attach数据中的订单id
                    if ("orderId".equals(pairs[0])) {
                        orderNo = pairs[1];
                    }
                }
                notify.setOrderNo(orderNo);
                notify.setMetadata(metadata);
            }
            event.setEventType(EventType.PAY_SUCCEEDED.getCode());
            return event;
        } catch (Exception e) {
            log.error("回调参数解析发生异常, {}", e);
        }
        return null;
    }
    /**
     * 构造一个预下单对象
     *
     * @param settings  微信支付配置项
     * @param payParams 客户端提交的支付请求参数
     * @param payment   支付对象
     * @return 预下单对象
     */
    private PrePayInfo buildPrePayInfo(FinanceWxSettings settings, PayParams payParams, Payment payment) {
        try {
            Map<String, Object> extras = payParams.getExtras();
            DateTime now = DateTime.now();
            PrePayInfo pay = PrePayInfo.builder()
                    .appId(settings.getWxAppId())
                    .nonceStr(UUID.randomUUID().toString().replaceAll("-", ""))
                    //通知url必须为直接可访问的url，不能携带参数
                    .notifyUrl(getNotifyUrl())
                    .mchId(settings.getMerchantId())
                    .body(payParams.getBody())
                    //这里用payId是因为微信不接受商户传来的重复的订单号，用户发起支付，取消后又发起支付，会发生此情况
                    .outTradeNo(payment.getPayId())
                    .totalFee(payParams.getAmount())
                    .spbillCreateIp(payParams.getClientIp())
                    .tradeType((extras != null && extras.get("trade_type") != null) ? extras.get("trade_type").toString() : "APP")
                    .key(settings.getApiSecret())
                    .signType("MD5")
                    .timeStart(now.toString("yyyyMMddHHmmss"))
                    //5分钟过期
                    .timeExpire(now.plusMinutes(5).toString("yyyyMMddHHmmss"))
                    .build();
            Map<String, Object> metadata = payParams.getMetadata();
            if (metadata == null) {
                metadata = new HashMap<>(0);
            }
            metadata.put("payId", payment.getPayId());
            //真正的订单号作为附加数据传入，回调时再解析出来注入
            metadata.put("orderId", payParams.getOrderNo());
            pay.setAttach(parameterAssemble(metadata, null));
            SortedMap<String, Object> parameters = BeanRefUtils.toSortedMap(pay, "key");
            String sign = sign(parameters, pay.parameterMap(), "MD5", settings.getApiSecret(), super.getCharset(), "sign");
            pay.setSign(sign);
            return pay;
        } catch (Exception e) {
            log.error("预支付参数构造发生异常, {}", e);
        }
        return null;
    }


    /**
     * 参数签名接口
     *
     * @param parameters   参数组装的SortedMap
     * @param parameterMap 字段对应的请求参数，传入null，或者字段名对应的value为null，则以字段名为准
     * @param signType     加密方式，MD5，Rsa，RSA2等
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
        sign = DigestUtils.md5Hex(content).toUpperCase();
        //拼接支付密钥
        log.info("签名结果：" + sign);
        return sign;
    }

    /**
     * 解析回调请求的参数
     *
     * @param request      回调请求对象
     * @param attributeMap 原始参数与实体类属性的映射关系
     * @return 参数键值对
     */
    @Override
    public Map<String, Object> parameterResolve(HttpServletRequest request, Map<String, Object> attributeMap) {
        String rawData = getRawData(request);
        if (StringUtils.isBlank(rawData)) {
            return null;
        }
        try {
            //解析参数(XML)格式
            Document document = DocumentHelper.parseText(rawData);
            //转换成Map对象
            return XmlUtil.toMap(new HashMap<>(16), document.getRootElement());
        } catch (Exception e) {
            log.error("参数转换发生异常, {}", e);
        }
        return null;
    }

    /**
     * 签名验证
     *
     * @param parameterMap 请求体，需要从中获取参数
     * @param signType     签名类型
     * @param charset      编码方式
     * @return 是否通过
     */
    @Override
    public boolean signVerify(Map<String, Object> parameterMap, String signType, String charset) {
        log.info("微信回调：{}", parameterMap);
        String wxSign = parameterMap.remove("sign").toString();
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(parameterMap.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = parameterMap.get(key).toString();
            content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
        }

        FinanceWxSettings settings = getWxSettingsByAppId(parameterMap.get("appid").toString());
        if (settings == null) {
            log.error("找不到应用配置");
            return false;
        }
        content.append("&key=").append(settings.getApiSecret().trim());
        log.info(content.toString());
        //拼接支付密钥
        String sign = DigestUtils.md5Hex(content.toString()).toUpperCase();
        log.info("微信回传签名：" + wxSign);
        log.info("系统签名结果：" + sign);
        boolean verify = sign.equals(wxSign);
        log.info("验签结果：" + verify);
        //验签后，将签名回传进去
        parameterMap.put("sign", sign);
        return verify;
    }

    @Override
    public String getNotifyUrl() {
        return super.getNotifyHost() + "/webhooks/finance/i/wx";
    }

    /**
     * 从请求体中获取参数
     *
     * @param request 请求对象
     * @return 原始数据
     */
    private String getRawData(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        InputStream stream = null;
        BufferedReader br = null;
        try {
            stream = request.getInputStream();
            br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while (true) {
                line = br.readLine();
                if (StringUtils.isBlank(line)) {
                    break;
                }
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            log.error("获取微信回调参数发生异常, {}", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("关闭流发生异常, {}", e);
            }
        }
        return null;
    }

}
