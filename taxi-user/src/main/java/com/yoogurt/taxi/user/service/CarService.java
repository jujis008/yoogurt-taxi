package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;

import java.util.List;

public interface CarService {
    ResponseObj saveCarInfo(CarInfo carInfo);
    CarInfo getCarInfo(Long carId);
    List<CarInfo> getCarByUserId(String userId);
    List<CarInfo> getCarByDriverId(String driverId);
    ResponseObj removeCar(Long carId);
}
