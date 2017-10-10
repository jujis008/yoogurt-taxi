package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.model.order.GiveBackOrderModel;
import com.yoogurt.taxi.order.form.GiveBackForm;
import com.yoogurt.taxi.order.service.GiveBackService;
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
public class GiveBackTests {

    @Autowired
    private GiveBackService giveBackService;

    @Test
    public void doGiveBackTest() {

        GiveBackForm form = new GiveBackForm();

        form.setLat(30.231651);
        form.setLng(120.564112154);
        form.setRealGiveBackAddress("杭州湾大酒店");
        form.setOrderId(17092615073534929L);
        GiveBackOrderModel model = giveBackService.doGiveBack(form);
        Assert.assertNotNull("还车失败！", model);
    }
}
