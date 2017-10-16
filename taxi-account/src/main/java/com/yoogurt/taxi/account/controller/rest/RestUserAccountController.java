package com.yoogurt.taxi.account.controller.rest;

import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.vo.ModificationVo;
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
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.enums.TradeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
            RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
            if (!userInfoRestResult.isSuccess()) {
                return RestResult.fail(StatusCode.BIZ_FAILED,"用户信息不存在");
            }
            Money receivableDeposit = new Money(0);
            UserInfo userInfo = userInfoRestResult.getBody();
            if (UserType.USER_APP_OFFICE.getCode() == userInfo.getType()) {
                receivableDeposit = new Money(Constants.OFFICE_RECEIVABLEDEPOSIT);
            }
            if (UserType.USER_APP_AGENT.getCode() == userInfo.getType()) {
                receivableDeposit = new Money(Constants.AGENT_RECEIVABLEDEPOSIT);
            }
            financeAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), receivableDeposit, userId);
        }
        if(financeAccount != null) return RestResult.success(financeAccount);
        return RestResult.fail(StatusCode.BIZ_FAILED, "账户信息不存在");
    }

    @RequestMapping(value = "/modification",method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public RestResult updateAccount(@Valid @RequestBody ModificationVo voObject) {
        TradeType tradeType = TradeType.getEnumsBycode(voObject.getType());
        if (tradeType == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED,"错误的交易类型");
        }
        RestResult<UserInfo> fineInUserInfoRestResult = restUserService.getUserInfoById(voObject.getInUserId());
        if (!fineInUserInfoRestResult.isSuccess()) {
            return fineInUserInfoRestResult;
        }
        FinanceAccount fineInFinanceAccount = financeAccountService.get(voObject.getInUserId());
        if (fineInFinanceAccount == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED,"账户资金不足，不足以扣款");
        }

        RestResult<UserInfo> fineOutUserInfoRestResult = restUserService.getUserInfoById(voObject.getOutUserId());
        if (!fineOutUserInfoRestResult.isSuccess()) {
            return fineOutUserInfoRestResult;
        }
        UserInfo fineOutUser = fineOutUserInfoRestResult.getBody();

        FinanceAccount fineOutFinanceAccount = financeAccountService.get(voObject.getOutUserId());

        if (fineOutFinanceAccount == null) {
            RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(voObject.getUserId());
            if (!userInfoRestResult.isSuccess()) {
                return RestResult.fail(StatusCode.BIZ_FAILED,"用户信息不存在");
            }
            Money receivableDeposit = new Money(0);
            UserInfo userInfo = userInfoRestResult.getBody();
            if (UserType.USER_APP_OFFICE.getCode() == userInfo.getType()) {
                receivableDeposit = new Money(Constants.OFFICE_RECEIVABLEDEPOSIT);
            }
            if (UserType.USER_APP_AGENT.getCode() == userInfo.getType()) {
                receivableDeposit = new Money(Constants.AGENT_RECEIVABLEDEPOSIT);
            }
            fineOutFinanceAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), receivableDeposit, voObject.getOutUserId());
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
                condition.setPayment(Payment.getEnumsBycode(voObject.getPayment()));
        }
        condition.setUserId(voObject.getUserId());
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeeAccount(fineInFinanceAccount.getAccountNo().toString());
        condition.setMoney(new Money(voObject.getMoney()));
        condition.setDraweeName(fineOutUser.getName());
        condition.setDraweeAccount(fineOutFinanceAccount.getAccountNo().toString());
        condition.setDraweePhone(fineOutUser.getUsername());
        condition.setContextId(voObject.getContextId());
        ResponseObj responseObj = financeAccountService.updateAccount(condition);
        return RestResult.of(responseObj);
    }
}
