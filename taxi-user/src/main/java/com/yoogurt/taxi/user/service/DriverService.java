package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;

public interface DriverService {
    ResponseObj saveDriverInfo(DriverInfo driverInfo);
    DriverInfo getDriverInfo(Long driverId);
    ResponseObj getDriverWebList(DriverWLCondition condition);
    ResponseObj removeDriver(Long driverId);
    DriverInfo getDriverByUserId(Long userId);
}
