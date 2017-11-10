package com.yoogurt.taxi.finance;

import com.yoogurt.taxi.common.utils.XmlUtil;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.finance.task.PayTask;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PayTests {

    @Autowired
    private PayService payService;

    @Autowired
    private WxPayService wxPayService;

    @Test
    public void submitTaskTest() {
        PayForm form = new PayForm();
        form.setAppId("app_driver");
        form.setAmount(1000L);
        form.setBody("来自Junit测试");
        form.setChannel("alipay");
        form.setOrderNo("1234567898");
        form.setSubject("测试一下");
        form.setClientIp("127.0.0.1");
        form.setMethod("yoogurt.taxi.finance.pay");
        form.setTimestamp(System.currentTimeMillis());
        form.setExtras(new HashMap<String, Object>(){{
            put("trade_type", "APP");
        }});
        form.setMetadata(new HashMap<String, Object>(){{
            put("type", "order");
        }});
        PayTask task = payService.submit(form);
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(task);
    }

    @Test
    public void test() {
        PayTask payTask = new PayTask();
        PayForm payForm = new PayForm();
        payForm.setAmount(100L);
        payForm.setBody("充值");
        payForm.setChannel("APP");
        payForm.setExtras(new HashMap<String, Object>(){{
            put("type", "order");
        }
        });
        payForm.setMetadata(new HashMap<String, Object>(){{
            put("type", "order");
        }});
        payForm.setOrderNo("1234567898");
        payForm.setAppId("app_agent");
        payForm.setSubject("测试一下");
        payForm.setClientIp("127.0.0.1");
        payForm.setMethod("yoogurt.taxi.finance.pay");
        payForm.setTimestamp(System.currentTimeMillis());
        payTask.setPayParams(payForm);
        wxPayService.doTask(payTask);
    }

    @Test
    public void getTaskTest() {
        PayTask payTask = payService.getTask("pt_17102711393874595");
        Assert.assertNotNull(payTask);
    }

    @Test
    public void getPaymentTest() {
        Payment payment = payService.queryResult("pt_17102711315495101");
        Assert.assertNotNull(payment);

    }

    @Test
    public void addPaymentTest() {
        Payment payment = new Payment();
        payment.setPayId("21436547");
        payment.setAppId("app_driver");
        Payment result = payService.addPayment(payment);
        Assert.assertNotNull(result);
    }

    public static void main(String[] args) throws DocumentException {
        String xml = "<xml>\n" +
                "    <sign>3E4C41DF3BE8FEC6D8B1C0DAF155801B</sign>\n" +
                "    <sign_type>MD5</sign_type>\n" +
                "    <nonce_str>69e7d89bab5449a6bf16fa1cf60a04b1</nonce_str>\n" +
                "    <mch_id>1491121432</mch_id>\n" +
                "    <trade_type>APP</trade_type>\n" +
                "    <body>充值</body>\n" +
                "    <spbill_create_ip>127.0.0.1</spbill_create_ip>\n" +
                "    <total_fee>100</total_fee>\n" +
                "    <time_start>20171109151328</time_start>\n" +
                "    <time_expire>20171109151828</time_expire>\n" +
                "    <out_trade_no>1234567898</out_trade_no>\n" +
                "    <attach>payId=pay_171109151328849&amp;type=order</attach>\n" +
                "    <notify_url>http://api.yoogate.cn/webhooks/finance/i/wx</notify_url>\n" +
                "    <appid>wx1956547190b7d0ef</appid>\n" +
                "</xml>";
        Document document = DocumentHelper.parseText(xml);
        Map<String, Object> map = XmlUtil.toMap(new HashMap<>(), document.getRootElement());
        System.out.println(map.get("attach").toString());
    }
}
