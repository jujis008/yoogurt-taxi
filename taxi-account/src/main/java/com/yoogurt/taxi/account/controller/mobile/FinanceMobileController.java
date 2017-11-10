package com.yoogurt.taxi.account.controller.mobile;

import com.yoogurt.taxi.account.form.ChargeDepositForm;
import com.yoogurt.taxi.account.form.PatchForm;
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
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.BillCondition;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.*;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mobile/account")
public class FinanceMobileController extends BaseController {
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private FinanceBillService financeBillService;
    @Autowired
    private RestUserService restUserService;

    @RequestMapping(value = "/bill/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getListApp(@Valid AccountListAppCondition condition, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        condition.setUserId(getUserId());
        Pager<FinanceBillListAppModel> financeBillListApp = financeBillService.getFinanceBillListApp(condition);
        return ResponseObj.success(financeBillListApp);
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getAccount() {
        Long userId = getUserId();
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.of(userInfoRestResult);
        }
        UserInfo userInfo = userInfoRestResult.getBody();
        if (!UserType.getEnumsByCode(userInfo.getType()).isAppUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            if (userInfo.getType().equals(UserType.USER_APP_OFFICE.getCode())) {
                financeAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(),new Money(Constants.OFFICE_RECEIVABLE_DEPOSIT),userId);
            }
            if (userInfo.getType().equals(UserType.USER_APP_AGENT.getCode())) {
                financeAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(),new Money(Constants.AGENT_RECEIVABLE_DEPOSIT),userId);
            }
        }
        return ResponseObj.success(financeAccount);
    }

    /**
     * 押金充值
     *
     * @param form 表单
     * @param result 验证结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/charge", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj chargeDeposit(@RequestBody @Valid ChargeDepositForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        Long userId = getUserId();
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(userInfoRestResult.getStatus(), userInfoRestResult.getMessage());
        }
        FinanceAccount financeAccount = financeAccountService.get(userId);
        if (financeAccount == null) {
            Money receivableDeposit = new Money(0);
            UserInfo userInfo = userInfoRestResult.getBody();
            if (UserType.USER_APP_OFFICE.getCode().equals(userInfo.getType())) {
                receivableDeposit = new Money(Constants.OFFICE_RECEIVABLE_DEPOSIT);
            }
            if (UserType.USER_APP_AGENT.getCode().equals(userInfo.getType())) {
                receivableDeposit = new Money(Constants.AGENT_RECEIVABLE_DEPOSIT);
            }
            financeAccount = financeAccountService.createAccount(RandomUtils.getPrimaryKey(), receivableDeposit, userId);
        }
        Money accountMoney = new Money(financeAccount.getReceivedDeposit());
        Money receivableDepositMoney = new Money(financeAccount.getReceivableDeposit());
        if (!receivableDepositMoney.greaterThan(accountMoney)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"押金充足，无需充值");
        }
        Money chargeMoney = receivableDepositMoney.subtract(accountMoney);
        UserInfo userInfo = userInfoRestResult.getBody();
        Payment payment = Payment.getEnumsBycode(form.getChargeType());
        if (payment == null || !payment.isChargeType()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "充值方式有误");
        }
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setDestinationType(DestinationType.DEPOSIT);
        condition.setPayeeAccount(financeAccount.getAccountNo().toString());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayment(payment);
        condition.setUserId(userId);
        condition.setTradeType(TradeType.CHARGE);
        ResponseObj responseObj = financeBillService.insertBill(chargeMoney, condition, payment, BillStatus.PENDING, BillType.DEPOSIT);
        if (!responseObj.isSuccess()) {
            return responseObj;
        }
        return responseObj;
    }

    /**
     * 取消充值
     * @param form 表单对象
     * @return
     */
    @RequestMapping(value = "/cancelCharge", method = RequestMethod.DELETE, produces = {"application/json;charset=utf-8"})
    public ResponseObj cancelCharge(@RequestBody PatchForm form) {
        if (form.getBillNo() == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"账单号不能为空");
        }
        financeBillService.chargeSuccessOrFailure(form.getBillNo(), BillStatus.FAIL);
        return ResponseObj.success();
    }

    /**
     * 提现：payment=1表示押金提现，payment=2表示余额提现
     *
     * @param form 表单
     * @param result 校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj withdraw(@RequestBody @Valid WithdrawForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        UserInfo userInfo = restUserService.getUserInfoById(getUserId()).getBody();
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();
        LocalTime startTime = LocalTime.parse(Constants.withdraw_start_time);
        LocalTime endTime = LocalTime.parse(Constants.withdraw_end_time);
        if (nowDate.getDayOfWeek().compareTo(DayOfWeek.of(Constants.withdraw_day_of_week)) != 0
                && nowTime.isBefore(startTime)
                && nowTime.isAfter(endTime)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"未在开放时间内");
        }
        BillCondition billCondition = new BillCondition();
        billCondition.setUserId(getUserId());
        LocalDateTime firstTimeOfWeek = LocalDateTime.of(nowDate.with(DayOfWeek.MONDAY), LocalTime.of(0, 0, 0));
        billCondition.setStartTime(Date.from(firstTimeOfWeek.atZone(ZoneId.systemDefault()).toInstant()));
        billCondition.setEndTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        List<FinanceBill> billList = financeBillService.getBillList(billCondition);
        if (billList.size()>=Constants.withdraw_times) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"本周已申请提现，无法重复提交申请。");
        }
        if (UserStatus.FROZEN == UserStatus.getEnumsByCode(userInfo.getStatus())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "账户已被冻结，不可操作");
        }
        if (!Encipher.matches(form.getPayPassword(), userInfo.getPayPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易密码有误");
        }
        FinanceAccount financeAccount = financeAccountService.get(getUserId());
        if (financeAccount == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "账户总资金不足");
        }
        Payment payment = Payment.getEnumsBycode(form.getPayment());
        if (payment == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "提现类型错误");
        }
        if (payment != Payment.DEPOSIT && payment != Payment.BALANCE) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "提现类型错误");
        }
        DestinationType destinationType = DestinationType.getEnumsBycode(form.getDestinationType());
        if (destinationType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "目的账户类型有误");
        }
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setTradeType(TradeType.WITHDRAW);
        condition.setPayment(payment);
        condition.setUserId(getUserId());
        condition.setPayeeName(form.getReservedName());
        condition.setPayeeAccount(form.getAccountNo());
        condition.setBankName(form.getBankName());
        condition.setBankAddress(form.getBankAddress());
        condition.setMoney(new Money(form.getWithdrawMoney()));
        condition.setDraweePhone(getUserName());
        condition.setDraweeAccount(financeAccount.getAccountNo().toString());
        condition.setDraweeName(userInfo.getName());
        condition.setDestinationType(destinationType);
        condition.setChangeType(AccountChangeType.frozen_add);
        return financeAccountService.updateAccount(condition);
    }

    @RequestMapping(value = "/i/withdraw/rule", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getWithdrawRule() {
        Map<String,Object> map = new HashMap<>();
        map.put("weekday", Constants.withdraw_day_of_week);
        map.put("startTime",Constants.withdraw_start_time);
        map.put("endTime",Constants.withdraw_end_time);
        map.put("times",Constants.withdraw_times);
        return ResponseObj.success(map);
    }

}
