package com.yoogurt.taxi.account.controller.mobile;

import com.yoogurt.taxi.account.form.ChargeDepositForm;
import com.yoogurt.taxi.account.form.WithdrawForm;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile/account")
public class UserAccountController extends BaseController{
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private FinanceBillService financeBillService;
    @Autowired
    private RestUserService restUserService;

    @RequestMapping(value = "/bill/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getListApp(@Validated RecordListAppCondition condition, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        condition.setUserId(getUserId());
        Pager<FinanceBillListModel> financeBillListApp = financeBillService.getFinanceBillListApp(condition);
        return ResponseObj.success(financeBillListApp);
    }

    /**
     * 押金充值
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/charge",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj chargeDeposit(@RequestBody @Validated ChargeDepositForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        Long userId = getUserId();
        Long billId = RandomUtils.getPrimaryKey();
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(userInfoRestResult.getStatus(),userInfoRestResult.getMessage());
        }
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            financeAccountService.createAccount(RandomUtils.getPrimaryKey(), new Money(Constants.receivableDeposit),userId);
        }
        UserInfo userInfo = userInfoRestResult.getBody();
        Payment payment = Payment.getEnumsBycode(form.getChargeType());
        if (payment == null || !payment.isChargeType()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"充值方式有误");
        }
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setBillType(BillType.DEPOSIT);
        condition.setDestinationType(DestinationType.DEPOSIT);
        condition.setPayeeAccount(financeAccount.getAccountNo().toString());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayment(payment);
        condition.setUserId(userId);
        condition.setTradeType(TradeType.CHARGE);
        condition.setBillId(billId);
        ResponseObj responseObj = financeBillService.insertBill(new Money(form.getChargeMoney()), condition, payment, BillStatus.PENDING);
        if (!responseObj.isSuccess()) {
            return responseObj;
        }
        return responseObj;
    }

    /**
     * 提现：payment=1表示押金提现，payment=2表示余额提现
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/withdraw",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj withdraw(@RequestBody @Validated WithdrawForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        UserInfo userInfo = restUserService.getUserInfoById(getUserId()).getBody();
        if (UserStatus.FROZEN == UserStatus.getEnumsByCode(userInfo.getStatus())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"账户已被冻结，不可操作");
        }
        if (!Encipher.matches(form.getPayPassword(),userInfo.getPayPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"交易密码有误");
        }
        FinanceAccount financeAccount = financeAccountService.get(getUserId());
        if (financeAccount == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"账户总资金不足");
        }
        Payment payment = Payment.getEnumsBycode(form.getPayment());
        if (payment == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"提现类型错误");
        }
        if (payment != Payment.DEPOSIT && payment != Payment.BALANCE) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"提现类型错误");
        }
        DestinationType destinationType = DestinationType.getEnumsBycode(form.getDestinationType());
        if (destinationType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"目的账户类型有误");
        }
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setTradeType(TradeType.WITHDRAW);
        condition.setPayment(payment);
        condition.setUserId(getUserId());
        condition.setPayeeName(form.getReservedName());
        condition.setPayeeAccount(form.getAccountNo());
        condition.setMoney(new Money(form.getWithdrawMoney()));
        condition.setDraweePhone(getUserName());
        condition.setDraweeAccount(financeAccount.getAccountNo().toString());
        condition.setDraweeName(userInfo.getName());
        condition.setDestinationType(destinationType);
        if (payment == Payment.DEPOSIT) {
            condition.setBillType(BillType.DEPOSIT);
        }
        if (payment == Payment.BALANCE) {
            condition.setBillType(BillType.BALANCE);
        }
        return financeAccountService.updateAccount(condition);
    }
}
