package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.beans.OrderHandoverInfo;
import com.yoogurt.taxi.dal.model.order.HandoverOrderModel;
import com.yoogurt.taxi.order.form.HandoverForm;
import com.yoogurt.taxi.order.service.HandoverService;
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
public class HandoverTests {

    @Autowired
    private HandoverService handoverService;

    @Test
    public void doHandoverTest() {

        HandoverForm handoverForm = new HandoverForm();
        handoverForm.setOrderId(17101612021383517L);
        handoverForm.setRealHandoverAddress("临丁路171号");
        handoverForm.setLat(30.5422331079);
        handoverForm.setLng(120.12348547011);
        HandoverOrderModel model = handoverService.doHandover(handoverForm);
        Assert.assertNotNull("交车不成功！", model);
    }

    @Test
    public void handoverInfoTest() {
        OrderHandoverInfo handoverInfo = handoverService.getHandoverInfo(17092615073534929L);
        Assert.assertNotNull("交车信息不存在！", handoverInfo);
    }
}
