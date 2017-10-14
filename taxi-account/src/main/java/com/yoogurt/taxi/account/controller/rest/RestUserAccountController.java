package com.yoogurt.taxi.account.controller.rest;

import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.enums.TradeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/rest/account")
public class RestUserAccountController {

    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private RestUserService restUserService;

    @RequestMapping(value = "/userId/{userId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") Long userId) {
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            financeAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), new Money(Constants.receivableDeposit), userId);
        }
        if(financeAccount != null) return RestResult.success(financeAccount);
        return RestResult.fail(StatusCode.BIZ_FAILED, "账户信息不存在");
    }

    @RequestMapping(value = "/modification",method = RequestMethod.POST)
    public RestResult updateAccount(Long fineInUserId, Long outUserId, Long contextId, BigDecimal money, Integer type, Integer payment) {
        TradeType tradeType = TradeType.getEnumsBycode(type);
        if (tradeType == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED,"错误的交易类型");
        }
        RestResult<UserInfo> fineInUserInfoRestResult = restUserService.getUserInfoById(fineInUserId);
        if (!fineInUserInfoRestResult.isSuccess()) {
            return fineInUserInfoRestResult;
        }
        FinanceAccount fineInFinanceAccount = financeAccountService.get(fineInUserId);
        if (fineInFinanceAccount == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED,"账户资金不足，不足以扣款");
        }

        RestResult<UserInfo> fineOutUserInfoRestResult = restUserService.getUserInfoById(outUserId);
        if (!fineOutUserInfoRestResult.isSuccess()) {
            return fineOutUserInfoRestResult;
        }
        UserInfo fineOutUser = fineOutUserInfoRestResult.getBody();

        FinanceAccount fineOutFinanceAccount = financeAccountService.get(outUserId);

        if (fineOutFinanceAccount == null) {
            fineOutFinanceAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), new Money(Constants.receivableDeposit), outUserId);
        }

        UserInfo userInfo = fineInUserInfoRestResult.getBody();
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setTradeType(tradeType);
        switch (tradeType) {
            case FINE_IN:
            case FINE_OUT:
                condition.setDestinationType(DestinationType.BALANCE);
                condition.setPayment(Payment.BALANCE);
            case INCOME:
                condition.setDestinationType(DestinationType.BALANCE);
                condition.setPayment(Payment.getEnumsBycode(payment));
        }
        condition.setUserId(fineInUserId);
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeeAccount(fineInFinanceAccount.getAccountNo().toString());
        condition.setMoney(new Money(money));
        condition.setDraweeName(fineOutUser.getName());
        condition.setDraweeAccount(fineOutFinanceAccount.getAccountNo().toString());
        condition.setDraweePhone(fineOutUser.getUsername());
        condition.setContextId(contextId);
        ResponseObj responseObj = financeAccountService.updateAccount(condition);
        return RestResult.of(responseObj);
    }
}
