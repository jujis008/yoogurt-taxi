package com.yoogurt.taxi.user.service.rest;

import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.user.service.rest.hystrix.RestOrderServiceHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "taxi-order", fallback = RestOrderServiceHystrix.class)
public interface RestOrderService {
    @RequestMapping(value = "rest/order/statistics/userId/{userId}",method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    RestResult<Map<String,Object>> statistics(@PathVariable(name = "userId") String userId);
}
