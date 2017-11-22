package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;

public interface DriverService {
    ResponseObj saveDriverInfo(DriverInfo driverInfo);
    DriverInfo getDriverInfo(String driverId);
    ResponseObj getDriverWebList(DriverWebListCondition condition);
    ResponseObj removeDriver(String driverId);
    DriverInfo getDriverByUserId(String userId);
}
