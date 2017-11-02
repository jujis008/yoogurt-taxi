package com.yoogurt.taxi.finance.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.utils.RSA;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceAlipaySettings;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.bo.alipay.Alipay;
import com.yoogurt.taxi.finance.dao.AlipaySettingsDao;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.AbstractFinanceBizService;
import com.yoogurt.taxi.finance.service.AlipayService;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("alipayService")
public class AlipayServiceImpl extends AbstractFinanceBizService implements AlipayService {

    @Autowired
    private PayService payService;

    @Autowired
    private AlipaySettingsDao alipaySettingsDao;

    @Override
    public FinanceAlipaySettings addAlipaySettings(FinanceAlipaySettings settings) {
        if (settings == null) return null;
        if (alipaySettingsDao.insertSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceAlipaySettings updateAlipaySettings(FinanceAlipaySettings settings) {
        if (settings == null) return null;
        if (alipaySettingsDao.updateByIdSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceAlipaySettings getAlipaySettings(String appId) {
        if (StringUtils.isBlank(appId)) return null;
        FinanceAlipaySettings probe = new FinanceAlipaySettings();
        probe.setAppId(appId);
        probe.setIsDeleted(Boolean.FALSE);
        return alipaySettingsDao.selectOne(probe);
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
                Payment payment = payService.buildPayment(payParams);
                final FinanceAlipaySettings settings = getAlipaySettings(appId);
                if (settings == null) {
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "该应用暂不支持支付宝支付");
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                //构造支付参数
                Alipay alipay = new Alipay();
                alipay.setAppId(settings.getAlipayAppId());
                alipay.setSellerId(settings.getSellerId());
                alipay.setSubject(payParams.getSubject());
                alipay.setBody(payParams.getBody());
                alipay.setOrderNo(payParams.getOrderNo());
                alipay.setTotalAmount(new Money(payParams.getAmount()).getAmount());
                alipay.setNotifyUrl(getNotifyUrl());
                alipay.setPassbackParams(mapper.writeValueAsString(payParams.getMetadata()));
                //biz_content 转换成 JSON格式，null字段去掉
                alipay.setBizContent(mapper.writeValueAsString(alipay));
                //将支付参数按ASCII升序排列
                SortedMap<String, Object> parameters = BeanRefUtils.toSortedMap(alipay);
                //生成签名
                String sign = sign(parameters, alipay.parameterMap(), RSA.RSA2_ALGORITHMS, settings.getPrivateKey(), "UTF-8", "sign", "key");
                log.info("支付宝签名：" + sign);
                if (StringUtils.isBlank(sign)) {
                    payment.setStatusCode(String.valueOf(StatusCode.BIZ_FAILED.getStatus()));
                    payment.setMessage("签名失败");
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "签名失败");
                }

                //将请求的URL进行编码处理
                String content = super.parameterAssemble(parameters, alipay.parameterMap(), "sign", "key") + "&sign=" + sign;
                String encodedContent = super.parameterEncode(content, super.getCharset());
                //构造 credential字段，用于客户端发起支付请求
                alipay.setSign(sign);
                Map<String, Object> credential = BeanRefUtils.toMap(alipay);
                credential.put("order_str", encodedContent);
                payment.setCredential(credential);
                return ResponseObj.success(payment);
            } catch (Exception e) {
                log.error("支付宝生成签名发生异常，{}", e);
            }
            return ResponseObj.fail();
        });
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
        if (parameters == null || parameters.size() <= 0) return null;
        String content = super.parameterAssemble(parameters, parameterMap, skipAttrs);
        return RSA.sign(content, signType, privateKey, charset);
    }

    @Override
    public String getNotifyUrl() {
        return "http://api.yoogate.cn/webhooks/finance/i/alipay";
    }
}
