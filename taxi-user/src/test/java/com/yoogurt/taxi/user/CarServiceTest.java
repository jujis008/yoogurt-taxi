package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.user.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CarServiceTest {
    @Autowired
    private CarService carService;

    @Test
    public void getCarByUserId() {
        Long userId = 0L;
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
        carInfo.setDriverId(0L);
        carInfo.setEnergyType(10);
        carInfo.setPlateNumber("");
        carInfo.setUserId(0L);
        carInfo.setVehicleRegisterTime(new Date());
        carInfo.setVehicleType("");
        carInfo.setVin("");
        ResponseObj responseObj = carService.saveCarInfo(carInfo);
        System.out.println(responseObj.toJSON());
    }

    @Test
    public void test() {
        Long driverId = 0L;
        List<CarInfo> carInfoList = carService.getCarByDriverId(driverId);
        carInfoList.forEach(car-> System.out.println(ResponseObj.success(car).toJSON()));
    }
}
