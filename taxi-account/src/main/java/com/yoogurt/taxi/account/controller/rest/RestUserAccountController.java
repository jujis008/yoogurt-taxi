package com.yoogurt.taxi.account.controller.rest;

import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
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

    @RequestMapping(value = "/userId/{userId}",method = RequestMethod.GET)
    public RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") Long userId) {
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            ResponseObj responseObj = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), new Money(Constants.receivableDeposit), userId);
            return RestResult.of(responseObj);
        }
        return RestResult.success(financeAccount);
    }

//    @RequestMapping(value = "/userId/{userId}",method = RequestMethod.POST)
//    public RestResult updateAccount(AccountUpdateCondition condition) {
//        ResponseObj responseObj = financeAccountService.updateAccount(condition);
//        return RestResult.of(responseObj);
//    }
}
