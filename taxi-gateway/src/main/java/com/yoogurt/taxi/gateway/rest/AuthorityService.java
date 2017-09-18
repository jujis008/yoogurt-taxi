package com.yoogurt.taxi.gateway.rest;

import com.yoogurt.taxi.common.vo.ResponseObj;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "taxi-user")
public interface AuthorityService {

    @RequestMapping(value = "/authorities/userId/{userId}", method = RequestMethod.GET)
    ResponseObj getAuthoritiesByUserId(@PathVariable(name = "userId") Long userId);

}