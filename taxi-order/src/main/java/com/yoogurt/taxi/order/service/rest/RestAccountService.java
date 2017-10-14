package com.yoogurt.taxi.order.service.rest;

import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.order.service.rest.hystrix.RestAccountServiceHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "taxi-account", fallback = RestAccountServiceHystrix.class)
public interface RestAccountService {

    @RequestMapping(value = "/rest/account/userId/{userId}", method = RequestMethod.GET)
    RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") Long userId);
}
