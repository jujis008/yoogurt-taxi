package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.bo.SmsPayload;
import com.yoogurt.taxi.dal.enums.SmsTemplateType;
import com.yoogurt.taxi.user.mq.SmsSender;
import com.yoogurt.taxi.user.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CarServiceTest {
    @Autowired
    private CarService carService;
    @Autowired
    private SmsSender smsSender;

    @Test
    public void sendTest() {
        SmsPayload payload = new SmsPayload();
        payload.setType(SmsTemplateType.agent_pwd);
        payload.setParam("123456");
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("17364517747");
//        phoneNumbers.add("18814892833");
        payload.setPhoneNumbers(phoneNumbers);
        smsSender.send(payload);
    }

    @Test
    public void getCarByUserId() {
        String userId = "0";
        List<CarInfo> carInfoList = carService.getCarByUserId(userId);
        carInfoList.forEach(car-> System.out.println(ResponseObj.success(car).toJSON()));
    }

    @Test
    public void getCarInfo() {
        Long carId = 1L;
        CarInfo carInfo = carService.getCarInfo(carId);
        System.out.println(ResponseObj.success(carInfo).toJSON());
    }

    @Test
    public void saveCarInfo() {
        CarInfo carInfo = new CarInfo();
        carInfo.setCarPicture("");
        carInfo.setCompany("");
        carInfo.setEnergyType(10);
        carInfo.setPlateNumber("");
        carInfo.setVehicleRegisterTime(new Date());
        carInfo.setVehicleType("");
        carInfo.setVin("");
        ResponseObj responseObj = carService.saveCarInfo(carInfo);
        System.out.println(responseObj.toJSON());
    }

}
