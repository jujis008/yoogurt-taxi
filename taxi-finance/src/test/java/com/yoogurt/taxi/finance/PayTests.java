package com.yoogurt.taxi.finance;

import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PayTests {

    @Autowired
    private PayService payService;

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
        Assert.assertNotNull(payment);
    }
}
