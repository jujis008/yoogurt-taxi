package com.yoogurt.taxi.account.service.rest;

import com.yoogurt.taxi.account.service.rest.hystrix.RestUserServiceImpl;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.UserInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "taxi-user", fallback = RestUserServiceImpl.class)
public interface RestUserService {

    @RequestMapping(value = "/rest/user/userId/{userId}", method = RequestMethod.GET)
    RestResult<UserInfo> getUserInfoById(@PathVariable(name = "userId") String userId);

}