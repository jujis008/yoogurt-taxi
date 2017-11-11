package com.yoogurt.taxi.order;


import com.yoogurt.taxi.dal.model.order.PickUpOrderModel;
import com.yoogurt.taxi.order.form.PickUpForm;
import com.yoogurt.taxi.order.service.PickUpService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PickUpTests {

    @Autowired
    private PickUpService pickUpService;

    @Test
    public void doPickUpTest() {
        PickUpForm form = new PickUpForm();
        form.setOrderId("17101612021383517");
        form.setCarStatus(Boolean.FALSE);
        form.setDescription("胎气不足");
        form.setPictures(new String[]{"http://yoogurt.aliyun-hz.com/xxxx/zzzz/test.jpg", "http://yoogurt.aliyun-hz.com/xxxx/zzzz/test2.jpg"});
        PickUpOrderModel model = pickUpService.doPickUp(form);
        Assert.assertNotNull("取车失败！", model);
    }
}
