package com.yoogurt.taxi.account.service.rest;

import com.yoogurt.taxi.account.service.rest.hystrix.RestOrderServiceHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "taxi-order", fallback = RestOrderServiceHystrix.class)
public interface RestOrderService {
}
