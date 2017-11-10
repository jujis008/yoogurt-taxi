package com.yoogurt.taxi.finance;

import com.yoogurt.taxi.common.utils.XmlUtil;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.params.PayParams;
import com.yoogurt.taxi.pay.service.PayService;
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
        PayParams form = new PayParams();
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
        PayParams payForm = new PayParams();
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

}
