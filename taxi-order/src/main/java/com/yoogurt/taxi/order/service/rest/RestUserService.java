package com.yoogurt.taxi.order.service.rest;

import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "taxi-user")
public interface RestUserService {

    @RequestMapping(value = "/rest/user/userId/{userId}", method = RequestMethod.GET)
    UserInfo getUserInfoById(@PathVariable(name = "userId") Long userId);

    @RequestMapping(value = "/driver/id/{id}", method = RequestMethod.GET)
    DriverInfo getDriverInfoById(@PathVariable(name = "driverId") Long driverId);

    @RequestMapping(value = "/driver/userId/{userId}", method = RequestMethod.GET)
    DriverInfo getDriverInfoByUserId(@PathVariable(name = "userId") Long userId);

    @RequestMapping(value = "/car/userId/{userId}", method = RequestMethod.GET)
    CarInfo getCarInfoByUserId(@PathVariable(name = "userId") Long userId);
}