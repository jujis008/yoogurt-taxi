package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.dal.enums.TrafficStatus;
import com.yoogurt.taxi.dal.enums.TrafficType;
import com.yoogurt.taxi.order.form.TrafficViolationForm;
import com.yoogurt.taxi.order.service.TrafficViolationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TrafficViolationTests {

    @Autowired
    private TrafficViolationService trafficViolationService;

    @Test
    public void getTrafficViolationsTest() {
        TrafficViolationListCondition condition = new TrafficViolationListCondition();
        condition.setOrderId("17101612021383517");
        condition.setStatus(TrafficStatus.PENDING.getCode());
        condition.setUserId("17101612021383517");
        BasePager<OrderTrafficViolationInfo> trafficViolations = trafficViolationService.getTrafficViolations(condition);
        Assert.assertTrue(trafficViolations.getDataList().size() == 1);

    }

    @Test
    public void buildTrafficViolationTest() {
        TrafficViolationForm form = new TrafficViolationForm();
        form.setAddress("却西路897号");
        form.setDescription("违规停车");
        form.setHappenTime(new Date());
        form.setType(TrafficType.TRAFFIC.getCode());
        form.setOrderId("17101612021383517");
        form.setUserId("17101612021383517");
        ResponseObj obj = trafficViolationService.buildTrafficViolation(form);
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }

    @Test
    public void addTrafficViolationTest() {
        TrafficViolationForm form = new TrafficViolationForm();
        form.setAddress("却西路897号");
        form.setDescription("违规停车");
        form.setHappenTime(new Date());
        form.setType(TrafficType.TRAFFIC.getCode());
        form.setOrderId("17101612021383517");
        form.setUserId("17101612021383517");
        ResponseObj obj = trafficViolationService.buildTrafficViolation(form);
        if (obj.isSuccess()) {
            Assert.assertNotNull("违章记录添加不成功", trafficViolationService.addTrafficViolation((OrderTrafficViolationInfo) obj.getBody()));
        }
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }
}
