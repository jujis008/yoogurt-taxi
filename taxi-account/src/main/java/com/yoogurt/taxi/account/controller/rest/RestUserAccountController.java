package com.yoogurt.taxi.account.controller.rest;

import com.yoogurt.taxi.dal.enums.*;
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

    @RequestMapping(value = "/userId/{userId}/{userType}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public RestResult<FinanceAccount> getAccountByUserId(@PathVariable(name = "userId") String userId, @PathVariable(name = "userType") Integer userType) {

        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            financeAccount = buildAccount(userId, userType);
        }
        if (financeAccount != null) {
            return RestResult.success(financeAccount);
        }
        return RestResult.fail(StatusCode.BIZ_FAILED, "账户信息不存在");
    }

    @RequestMapping(value = "/modification", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public RestResult updateAccount(@Valid @RequestBody ModificationVo voObject) {
        TradeType tradeType = TradeType.getEnumsByCode(voObject.getType());
        if (tradeType == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED, "错误的交易类型");
        }
        RestResult<UserInfo> fineInUserInfoRestResult = restUserService.getUserInfoById(voObject.getInUserId());
        if (!fineInUserInfoRestResult.isSuccess()) {
            return fineInUserInfoRestResult;
        }
        FinanceAccount fineInFinanceAccount = financeAccountService.get(voObject.getInUserId());
        if (fineInFinanceAccount == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED, "账户资金不足，不足以扣款");
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
                return RestResult.fail(StatusCode.BIZ_FAILED, "用户信息不存在");
            }
            UserInfo userInfo = userInfoRestResult.getBody();
            fineOutFinanceAccount = buildAccount(voObject.getOutUserId(), userInfo.getType());
        }
        if (fineOutFinanceAccount == null) {
            return RestResult.fail(StatusCode.BIZ_FAILED, "账户创建失败");
        }

        UserInfo userInfo = fineInUserInfoRestResult.getBody();
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setTradeType(tradeType);
        switch (tradeType) {
            case FINE_IN:
            case FINE_OUT:
            case OUTCOME:
                condition.setDestinationType(DestinationType.BALANCE);
                condition.setPayment(Payment.BALANCE);
                break;
            case INCOME:
            case REFUND:
                condition.setDestinationType(DestinationType.BALANCE);
                condition.setPayment(Payment.getEnumsBycode(voObject.getPayment()));
                break;
            default:
                return RestResult.fail(StatusCode.BIZ_FAILED, "错误的交易类型");
        }
        condition.setUserId(voObject.getUserId());
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeeAccount(fineInFinanceAccount.getAccountNo());
        condition.setMoney(new Money(voObject.getMoney()));
        condition.setDraweeName(fineOutUser.getName());
        condition.setDraweeAccount(fineOutFinanceAccount.getAccountNo());
        condition.setDraweePhone(fineOutUser.getUsername());
        condition.setContextId(voObject.getContextId());
        ResponseObj responseObj = financeAccountService.updateAccount(condition);
        return RestResult.of(responseObj);
    }

    private FinanceAccount buildAccount(String userId, Integer userType) {
        Money receivableDeposit;
        switch (UserType.getEnumsByCode(userType)) {
            case USER_APP_AGENT:
                receivableDeposit = new Money(Constants.AGENT_RECEIVABLE_DEPOSIT);
                break;
            case USER_APP_OFFICE:
                receivableDeposit = new Money(Constants.OFFICE_RECEIVABLE_DEPOSIT);
                break;
            default:
                return null;
        }
        return financeAccountService.createAccount(RandomUtils.getPrimaryKey(), receivableDeposit, userId);
    }
}
