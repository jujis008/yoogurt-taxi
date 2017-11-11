package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.model.order.AcceptOrderModel;
import com.yoogurt.taxi.order.form.AcceptForm;
import com.yoogurt.taxi.order.service.AcceptService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AcceptTests {

    @Autowired
    private AcceptService acceptService;

    @Test
    public void doAcceptTest() {
        AcceptForm acceptForm = new AcceptForm();
        acceptForm.setCarStatus(Boolean.FALSE);
        acceptForm.setDescription("大灯有裂缝");
        acceptForm.setOrderId("17101612021383517");
        acceptForm.setPictures(new String[]{"http://yoogurt.aliyun-hz.com/xxxx/p/test.jpg", "http://yoogurt.aliyun-hz.com/xxxx/p/test2.jpg"});
        AcceptOrderModel model = acceptService.doAccept(acceptForm);
        Assert.assertNotNull("收车失败！", model);
    }
}
