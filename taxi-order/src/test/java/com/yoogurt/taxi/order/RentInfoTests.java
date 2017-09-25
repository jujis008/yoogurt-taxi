package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentInfoTests {

    @Autowired
    private RentInfoService rentInfoService;

    @Test
    public void addRentInfoTest() {
        RentForm rentForm = new RentForm();
        rentForm.setAddress("江干区幸福南路1118号");
        rentForm.setGiveBackTime(new Date());
        rentForm.setHandoverTime(new Date());
        rentForm.setLat(30.263684012);
        rentForm.setLng(120.365214521);
        rentForm.setPrice(new BigDecimal(120));
        rentForm.setUserId(8888L);
        ResponseObj rentInfo = rentInfoService.addRentInfo(rentForm);
        Assert.assertNotNull("租单发布失败", rentInfo.getBody());
    }
}
