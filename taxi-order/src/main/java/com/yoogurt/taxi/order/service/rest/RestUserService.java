package com.yoogurt.taxi.order.service.rest;

import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.order.service.rest.hystrix.RestUserServiceHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "taxi-user", fallback = RestUserServiceHystrix.class)
public interface RestUserService {

    @RequestMapping(value = "/rest/user/userId/{userId}", method = RequestMethod.GET)
    RestResult<UserInfo> getUserInfoById(@PathVariable(name = "userId") Long userId);

    @RequestMapping(value = "/rest/user/driver/id/{id}", method = RequestMethod.GET)
    RestResult<DriverInfo> getDriverInfoById(@PathVariable(name = "driverId") Long driverId);

    @RequestMapping(value = "/rest/user/driver/userId/{userId}", method = RequestMethod.GET)
    RestResult<DriverInfo> getDriverInfoByUserId(@PathVariable(name = "userId") Long userId);

    @RequestMapping(value = "/rest/user/car/userId/{userId}", method = RequestMethod.GET)
    RestResult<List<CarInfo>> getCarInfoByUserId(@PathVariable(name = "userId") Long userId);
}