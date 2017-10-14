package com.yoogurt.taxi.account.controller.rest;

import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/account")
public class RestUserAccountController {

    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/userId/{userId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") Long userId) {
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            return RestResult.fail(StatusCode.REST_FAIL, "账户信息不存在");
        }
        return RestResult.success(financeAccount);
    }

}
