package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceAccountDao;
import com.yoogurt.taxi.account.mq.NotificationSender;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    private FinanceAccountDao financeAccountDao;
    @Autowired
    private FinanceRecordService financeRecordService;
    @Autowired
    private FinanceBillService financeBillService;
    @Autowired
    private RestUserService restUserService;
    @Autowired
    private NotificationSender notificationSender;

    @Override
    public FinanceAccount get(String userId) {
        return financeAccountDao.selectById(userId);
    }

    @Override
    public FinanceAccount createAccount(String accountNo, Money receivableDeposit, String userId) {
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return null;
        }
        UserInfo userInfo = userInfoRestResult.getBody();
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAccountNo(accountNo);
        financeAccount.setBalance(new BigDecimal(0));
        financeAccount.setFrozenBalance(new BigDecimal(0));
        financeAccount.setFrozenDeposit(new BigDecimal(0));
        financeAccount.setReceivableDeposit(receivableDeposit.getAmount());
        financeAccount.setReceivedDeposit(new BigDecimal(0));
        financeAccount.setName(userInfo.getName());
        financeAccount.setUsername(userInfo.getUsername());
        financeAccount.setUserType(userInfo.getType());
        financeAccount.setUserId(userId);
        //创建默认资金数账户
        financeAccountDao.insert(financeAccount);
        return financeAccount;
    }

    /**
     * 更新账户，具体功能有：1.充值（提现申请），2.提现（回调，即将冻结金额扣除，账户不动）
     * 3.罚款，4.补偿，5.订单收入
     *
     * @param condition 条件参数
     * @return ResponseObj
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObj updateAccount(AccountUpdateCondition condition) {

        Money money = condition.getMoney();
        if (money.getCent() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "金额错误");
        }
        String userId = condition.getUserId();
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户不存在");
        }
        synchronized (userId.toString().intern()) {
            FinanceAccount financeAccount = financeAccountDao.selectById(userId);
            TradeType tradeType = condition.getTradeType();
            //账户不存在
            if (financeAccount == null) {
                //创建账户
                financeAccount = createAccount(RandomUtils.getPrimaryKey(), new Money(0), userId);
            }
            Payment payment = condition.getPayment();
            if (condition.getChangeType() == null) {
                //判断账户的有效性
                ResponseObj responseObj = this.validateAccount(financeAccount, tradeType, money, payment);
                if (!responseObj.isSuccess()) {
                    return responseObj;
                }
            }
            switch (tradeType) {
                //提现
                case WITHDRAW:
                    BillType billType;
                    //余额提现
                    if (payment == Payment.BALANCE) {

                        billType = BillType.BALANCE;
                        Money frozenBalance = new Money(financeAccount.getFrozenBalance());
                        Money balance = new Money(financeAccount.getBalance());
                        //提现申请，放入冻结
                        if (condition.getChangeType() == AccountChangeType.frozen_add) {
                            financeAccount.setFrozenBalance(frozenBalance.add(money).getAmount());
                            financeAccount.setBalance(balance.subtract(money).getAmount());
                            /*1.更新账户*/
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.insertBill(money, condition, payment, BillStatus.PENDING, billType);
                            return ResponseObj.success(financeAccount);
                        }
                        //提现成功，冻结扣除
                        if (condition.getChangeType() == AccountChangeType.frozen_deduct) {
                            FinanceBill financeBill = financeBillService.get(condition.getBillId());
                            if (financeBill == null) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账单不存在");
                            }
                            if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "已处理");
                            }
                            if (billType != BillType.getEnumsByCode(financeBill.getBillType())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "非法操作");
                            }
                            financeAccount.setFrozenBalance(frozenBalance.subtract(new Money(financeBill.getAmount())).getAmount());

                            financeBill.setBillStatus(BillStatus.SUCCESS.getCode());
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.save(financeBill);
                            FinanceRecord financeRecord = new FinanceRecord();
                            financeRecord.setBillId(financeBill.getId());
                            financeRecord.setBillNo(financeBill.getBillNo());
                            financeRecord.setStatus(BillStatus.SUCCESS.getCode());
                            financeRecordService.save(financeRecord);
                            return ResponseObj.success(financeAccount);
                        }
                        //提现失败，冻结返回
                        if (condition.getChangeType() == AccountChangeType.frozen_back) {
                            FinanceBill financeBill = financeBillService.get(condition.getBillId());
                            if (financeBill == null) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账单不存在");
                            }
                            if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "已处理");
                            }
                            if (billType != BillType.getEnumsByCode(financeBill.getBillType())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "非法操作");
                            }
                            financeAccount.setFrozenBalance(frozenBalance.subtract(new Money(financeBill.getAmount())).getAmount());
                            financeAccount.setBalance(balance.add(new Money(financeBill.getAmount())).getAmount());

                            financeBill.setBillStatus(BillStatus.FAIL.getCode());
                            financeBill.setDescription(condition.getRemark());
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.save(financeBill);
                            FinanceRecord financeRecord = new FinanceRecord();
                            financeRecord.setBillId(financeBill.getId());
                            financeRecord.setBillNo(financeBill.getBillNo());
                            financeRecord.setStatus(BillStatus.FAIL.getCode());
                            financeRecordService.save(financeRecord);
                            return ResponseObj.success(financeAccount);
                        }
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "余额提现操作有误");

                    } else if (payment == Payment.DEPOSIT) {
                        billType = BillType.DEPOSIT;
                        Money frozenDeposit = new Money(financeAccount.getFrozenDeposit());
                        Money receivedDeposit = new Money(financeAccount.getReceivedDeposit());
                        //提现申请,放入冻结
                        if (condition.getChangeType() == AccountChangeType.frozen_add) {
                            financeAccount.setFrozenDeposit(frozenDeposit.add(money).getAmount());
                            financeAccount.setReceivedDeposit(receivedDeposit.subtract(money).getAmount());
                            /*1.更新账户*/
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.insertBill(money, condition, payment, BillStatus.PENDING, billType);
                            return ResponseObj.success(financeAccount);
                        }
                        //提现成功，扣除冻结
                        if (condition.getChangeType() == AccountChangeType.frozen_deduct) {
                            FinanceBill financeBill = financeBillService.get(condition.getBillId());
                            if (financeBill == null) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账单不存在");
                            }
                            if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "已处理");
                            }
                            if (billType != BillType.getEnumsByCode(financeBill.getBillType())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "非法操作");
                            }
                            financeAccount.setFrozenDeposit(frozenDeposit.subtract(new Money(financeBill.getAmount())).getAmount());

                            financeBill.setBillStatus(BillStatus.SUCCESS.getCode());
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.save(financeBill);
                            FinanceRecord financeRecord = new FinanceRecord();
                            financeRecord.setBillId(financeBill.getId());
                            financeRecord.setBillNo(financeBill.getBillNo());
                            financeRecord.setStatus(BillStatus.SUCCESS.getCode());
                            financeRecordService.save(financeRecord);
                            return ResponseObj.success(financeAccount);
                        }
                        //提现失败，冻结返回
                        if (condition.getChangeType() == AccountChangeType.frozen_back) {
                            FinanceBill financeBill = financeBillService.get(condition.getBillId());
                            if (financeBill == null) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账单不存在");
                            }
                            if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "已处理");
                            }
                            if (billType != BillType.getEnumsByCode(financeBill.getBillType())) {
                                return ResponseObj.fail(StatusCode.BIZ_FAILED, "非法操作");
                            }
                            financeAccount.setFrozenDeposit(frozenDeposit.subtract(new Money(financeBill.getAmount())).getAmount());
                            financeAccount.setReceivedDeposit(receivedDeposit.add(new Money(financeBill.getAmount())).getAmount());

                            financeBill.setBillStatus(BillStatus.FAIL.getCode());
                            financeBill.setDescription(condition.getRemark());
                            financeAccountDao.updateById(financeAccount);
                            financeBillService.save(financeBill);
                            FinanceRecord financeRecord = new FinanceRecord();
                            financeRecord.setBillId(financeBill.getId());
                            financeRecord.setBillNo(financeBill.getBillNo());
                            financeRecord.setStatus(BillStatus.FAIL.getCode());
                            financeRecordService.save(financeRecord);
                            return ResponseObj.success(financeAccount);
                        }
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "押金提现操作有误");
                    } else {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "不支持的提现类型");
                    }
                //充值(回调时使用)
                case CHARGE:
                    //目前只支持押金充值
                    if (condition.getDestinationType() != DestinationType.DEPOSIT) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "目前只支持押金充值");
                    }
                    financeAccount.setReceivedDeposit(new Money(financeAccount.getReceivedDeposit()).add(money).getAmount());
                    FinanceBill financeBill = financeBillService.get(condition.getBillId());
                    if (financeBill == null) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "充值记录不存在");
                    }
                    if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "该充值记录已处理");
                    }
                    if (!financeBill.getAmount().equals(money.getAmount())) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "充值记录异常");
                    }
                    financeBill.setBillType(BillType.DEPOSIT.getCode());
                    financeBill.setDraweePhone(condition.getDraweePhone());
                    financeBill.setDraweeName(condition.getDraweeName());
                    financeBill.setDraweeAccount(condition.getDraweeAccount());
                    financeBill.setPayment(condition.getPayment().getCode());
                    financeBill.setTransactionNo(condition.getTransactionNo());
                    financeBill.setBillStatus(BillStatus.SUCCESS.getCode());
                    /*更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    /*更新账单状态*/
                    financeBillService.save(financeBill);

                    /*3.插入账单记录*/
                    FinanceRecord financeRecord = new FinanceRecord();
                    financeRecord.setBillId(financeBill.getId());
                    financeRecord.setBillNo(financeBill.getBillNo());
                    financeRecord.setStatus(BillStatus.SUCCESS.getCode());
                    financeRecord.setRemark("充值成功");
                    financeRecordService.save(financeRecord);
                    return ResponseObj.success(financeAccount);
                //补偿,余额增加
                case FINE_IN:
                    financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
                    /*更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                    return ResponseObj.success(financeAccount);
                //罚款
                case FINE_OUT:
                case OUTCOME:
                    //余额
                    Money balance = new Money(financeAccount.getBalance());
                    //剩余押金
                    Money deposit = new Money(financeAccount.getReceivedDeposit());
                    //余额充足，从余额里扣
                    if (!money.greaterThan(balance)) {
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);
                        financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                    } else {
                        //余额扣光
                        financeAccount.setBalance(new Money(0).getAmount());
                        financeAccount.setReceivedDeposit(deposit.add(balance).subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);
                        //余额没钱，不用插入记录，全部从押金扣
                        if (!balance.equals(new Money(0))) {
                            financeBillService.insertBill(balance, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                        }
                        financeBillService.insertBill(money.subtract(balance), condition, Payment.DEPOSIT, BillStatus.SUCCESS, BillType.DEPOSIT);
                    }
                    return ResponseObj.success(financeAccount);
                //订单收入,余额增加
                case INCOME:
                //订单退款，余额增加
                case REFUND:
                    financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
                    /*更新或插入账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, condition.getPayment(), BillStatus.SUCCESS, BillType.BALANCE);
                    return ResponseObj.success(financeAccount);
                default:
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易类型不存在");
            }
        }
    }

    @Override
    public BasePager<FinanceAccountListModel> getListWeb(AccountListWebCondition condition) {
        return financeAccountDao.getListWeb(condition);
    }

    @Override
    public ResponseObj handleWithdraw(Long billId, BillStatus billStatus) {
        FinanceBill financeBill = financeBillService.get(billId);
        if (financeBill == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "操作对象不存在，请刷新重试");
        }
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setBillId(billId);
        condition.setUserId(financeBill.getUserId());
        condition.setMoney(new Money(financeBill.getAmount()));
        condition.setTradeType(TradeType.WITHDRAW);
        condition.setPayment(Payment.getEnumsBycode(financeBill.getPayment()));
        UserType userType = UserType.getEnumsByCode(financeBill.getUserType());
        String title = "提现通知";
        if (userType == UserType.USER_APP_OFFICE) {
            title = Constants.OFFICIAL_APP_NAME;
        }
        if (userType == UserType.USER_APP_AGENT) {
            title = Constants.AGENT_APP_NAME;
        }
        Map<String,Object> extras = new HashMap<>(1);
        if (financeBill.getBillType().equals(BillType.BALANCE.getCode())) {
            extras.put("type","balance");
        }
        if (financeBill.getBillType().equals(BillType.DEPOSIT.getCode())) {
            extras.put("type","deposit");
        }
        switch (billStatus) {
            //转账成功，减少冻结资金
            case SUCCESS:
                condition.setChangeType(AccountChangeType.frozen_deduct);
                ResponseObj responseObj = updateAccount(condition);
                if (responseObj.isSuccess()) {
                    PushPayload payload = new PushPayload(userType, SendType.WITHDRAW_SUCCESS, title);
                    payload.addUserId(financeBill.getUserId());
                    payload.setExtras(extras);
                    notificationSender.send(payload);
                }
                return responseObj;
            case FAIL:
                condition.setChangeType(AccountChangeType.frozen_back);
                ResponseObj obj = updateAccount(condition);
                if (obj.isSuccess()) {
                    PushPayload payload = new PushPayload(userType, SendType.WITHDRAW_FAILED, title);
                    payload.addUserId(financeBill.getUserId());
                    payload.setExtras(extras);
                    notificationSender.send(payload);
                }
                return obj;
            default:
                return ResponseObj.fail(StatusCode.FORM_INVALID, "操作标识不正确");
        }
    }

    private ResponseObj validateAccount(FinanceAccount financeAccount, TradeType tradeType, Money money, Payment payment) {
        //扣除资金
        if (!tradeType.isAdd()) {
            //扣除资金额大于账户总资金(罚款)
            if (money.greaterThan(new Money(financeAccount.getBalance()).add(new Money(financeAccount.getReceivedDeposit())))) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账户资金不足");
            }
            //提现
            if (tradeType == TradeType.WITHDRAW) {
                //余额提现
                if (payment == Payment.BALANCE) {
                    if (money.greaterThan(new Money(financeAccount.getBalance()))) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "余额提现，余额不足");
                    }
                }
                //押金提现
                if (payment == Payment.DEPOSIT) {
                    if (money.greaterThan(new Money(financeAccount.getReceivedDeposit()))) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "押金提现，押金不足");
                    }
                }
            }
        }
        return ResponseObj.success();
    }

}
