package com.yoogurt.taxi.account.service.rest.hystrix;

import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class RestUserServiceHystrix implements RestUserService {

    @Override
    public RestResult<UserInfo> getUserInfoById(String userId) {
        return RestResult.fail(StatusCode.REST_FAIL, "获取用户资料失败");
    }

//    @Override
//    public RestResult<DriverInfo> getDriverInfoById(Long driverId) {
//        return RestResult.fail(StatusCode.REST_FAIL, "获取司机信息失败");
//    }
//
//    @Override
//    public RestResult<DriverInfo> getDriverInfoByUserId(Long userId) {
//        return RestResult.fail(StatusCode.REST_FAIL, "获取司机信息失败");
//    }
//
//    @Override
//    public RestResult<List<CarInfo>> getCarInfoByUserId(Long userId) {
//        return RestResult.fail(StatusCode.REST_FAIL, "获取车辆信息失败");
//    }
}
