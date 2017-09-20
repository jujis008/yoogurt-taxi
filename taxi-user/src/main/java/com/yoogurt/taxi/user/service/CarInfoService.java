package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CarInfo;

import java.util.List;

public interface CarInfoService {
    ResponseObj saveCarInfo(CarInfo carInfo);
    CarInfo getCarInfo(Long carId);
    List<CarInfo> getCarByUserId(Long userId);
    List<CarInfo> getCarByDriverId(Long driverId);
    ResponseObj removeCar(Long carId);
}
