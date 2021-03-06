package com.yoogurt.taxi.order.service.rest;

import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.vo.ModificationVo;
import com.yoogurt.taxi.order.service.rest.hystrix.RestAccountServiceImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(value = "taxi-account", fallback = RestAccountServiceImpl.class)
public interface RestAccountService {

    @RequestMapping(value = "/rest/account/userId/{userId}/{userType}", method = RequestMethod.GET)
    RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") String userId, @PathVariable(name = "userType")Integer userType);

    @RequestMapping(value = "/rest/account/modification", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    RestResult updateAccount(@Valid @RequestBody ModificationVo vo);
}
