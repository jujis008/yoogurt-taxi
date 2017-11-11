package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentInfoTests {

    @Autowired
    private RentInfoService rentInfoService;

    @Test
    public void addRentInfoTest() {
        RentForm rentForm = new RentForm();
        rentForm.setAddress("江干区幸福南路1118号");
        rentForm.setHandoverTime(new DateTime().plusDays(19).toDate());
        rentForm.setGiveBackTime(new DateTime().plusDays(22).toDate());
        rentForm.setLat(30.20684012);
        rentForm.setLng(120.36450212);
        rentForm.setPrice(new BigDecimal(125));
        rentForm.setUserId("17101612021383517");
        ResponseObj result = rentInfoService.addRentInfo(rentForm);
        Assert.assertNotNull(result.getMessage(), result.getBody());
    }

    @Test
    public void getRentInfo() {
        RentInfo rentInfo = rentInfoService.getRentInfo("17101612021383517", "17101612021383517");
        Assert.assertNotNull("租单信息不存在", rentInfo);
    }

    @Test
    public void getRentPOIs() {
        RentPOICondition condition = new RentPOICondition();
//        condition.setMaxLat(30.9866632);
//        condition.setMinLat(29.89663321);
//        condition.setMaxLng(121.563121);
//        condition.setMinLng(120.1123654);
//        condition.setKeywords("临丁路");
//        condition.setStartTime(new DateTime().minusDays(2).toDate());
//        condition.setEndTime(new Date());

        List<RentInfoModel> rentList = rentInfoService.getRentList(condition);
        Assert.assertNotNull("租单信息不存在", rentList);
    }

    @Test
    public void getRentList() {

        RentListCondition condition = new RentListCondition();
//        condition.setMaxLat(30.9866632);
//        condition.setMinLat(29.89663321);
//        condition.setMaxLng(121.563121);
//        condition.setMinLng(120.1123654);
//        condition.setKeywords("临丁路");
//        condition.setStartTime(new DateTime().minusDays(2).toDate());
//        condition.setEndTime(new Date());
//        condition.setPageNum(1);
//        condition.setPageSize(15);
//        condition.setSortName("price");
//        condition.setSortOrder("ASC");
        Pager<RentInfoModel> pager = rentInfoService.getRentListByPage(condition);
        Assert.assertNotNull("租单信息不存在", pager);
    }
}
