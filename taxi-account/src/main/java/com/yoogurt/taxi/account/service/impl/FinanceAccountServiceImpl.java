package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceAccountDao;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    @Override
    public FinanceAccount get(Long userId) {
        return financeAccountDao.selectById(userId);
    }

    @Override
    public FinanceAccount createAccount(Long accountNo, Money receivableDeposit, Long userId) {
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
        financeAccountDao.insert(financeAccount);//创建默认资金数账户
        return financeAccount;
    }

    /**
     * 更新账户，具体功能有：1.充值（提现申请），2.提现（回调，即将冻结金额扣除，账户不动）
     * 3.罚款，4.补偿，5.订单收入
     * @param condition
     * @return
     */
    @Transactional
    @Override
    public ResponseObj updateAccount(AccountUpdateCondition condition) {

        Money money = condition.getMoney();
        if (money.getCent() == 0) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "金额错误");
        }
        Long userId = condition.getUserId();
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(userId);
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户不存在");
        }
        synchronized (userId.toString().intern()) {
            FinanceAccount financeAccount = financeAccountDao.selectById(userId);
            TradeType tradeType = condition.getTradeType();
            if (financeAccount == null) {//账户不存在
                //创建账户
                createAccount(RandomUtils.getPrimaryKey(), new Money(0), userId);
            }
            Payment payment = condition.getPayment();
            //判断账户的有效性
            ResponseObj responseObj = this.validateAccount(financeAccount, tradeType, money, payment);
            if (!responseObj.isSuccess()) {
                return responseObj;
            }
            switch (tradeType) {
                case WITHDRAW://提现(提现申请时，提现回调另做)
                    BillType billType = null;
                    if (payment == Payment.BALANCE) {//余额提现
                        Money balance = new Money(financeAccount.getBalance());
                        Money frozenBalance = new Money(financeAccount.getFrozenBalance());
                        financeAccount.setFrozenBalance(frozenBalance.add(money).getAmount());
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                        billType = BillType.BALANCE;
                    } else if (payment == Payment.DEPOSIT) {
                        Money receivedDeposit = new Money(financeAccount.getReceivedDeposit());
                        Money frozenDeposit = new Money(financeAccount.getFrozenDeposit());
                        financeAccount.setFrozenDeposit(frozenDeposit.add(money).getAmount());
                        financeAccount.setReceivedDeposit(receivedDeposit.subtract(money).getAmount());
                        billType = BillType.DEPOSIT;
                    } else {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"不支持的提现类型");
                    }
                    /**1.更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, payment, BillStatus.PENDING, billType);
                    return ResponseObj.success(financeAccount);
                case CHARGE://充值(回调时使用)
                    if (condition.getDestinationType() != DestinationType.DEPOSIT) {//目前只支持押金充值
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "目前只支持押金充值");
                    }
                    financeAccount.setReceivedDeposit(new Money(financeAccount.getReceivedDeposit()).add(money).getAmount());
                    FinanceBill financeBill = financeBillService.get(condition.getBillId());
                    if (financeBill == null) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"充值记录不存在");
                    }
                    if (financeBill.getBillStatus() != BillStatus.PENDING.getCode()) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"该充值记录已处理");
                    }
                    if (!financeBill.getAmount().equals(money.getAmount())) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"充值记录异常");
                    }
                    financeBill.setBillType(BillType.DEPOSIT.getCode());
                    financeBill.setDraweePhone(condition.getDraweePhone());
                    financeBill.setDraweeName(condition.getDraweeName());
                    financeBill.setDraweeAccount(condition.getDraweeAccount());
                    financeBill.setPayment(condition.getPayment().getCode());
                    financeBill.setTransactionNo(condition.getTransactionNo());
                    financeBill.setBillStatus(BillStatus.SUCCESS.getCode());
                    /**更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    /**更新账单状态*/
                    financeBillService.save(financeBill);

                    /**3.插入账单记录*/
                    FinanceRecord financeRecord = new FinanceRecord();
                    financeRecord.setBillId(financeBill.getId());
                    financeRecord.setBillNo(financeBill.getBillNo());
                    financeRecord.setStatus(BillStatus.SUCCESS.getCode());
                    financeRecordService.save(financeRecord);
                    return ResponseObj.success(financeAccount);
                case FINE_IN://补偿,余额增加
                    financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
                    /**更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                    return ResponseObj.success(financeAccount);
                case FINE_OUT://罚款
                    Money balance = new Money(financeAccount.getBalance());//余额
                    Money deposit = new Money(financeAccount.getReceivedDeposit());//剩余押金
                    if (!money.greaterThan(balance)) {//余额充足，从余额里扣
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);
                        financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                    } else {
                        financeAccount.setBalance(new Money(0).getAmount());//余额扣光
                        financeAccount.setReceivedDeposit(deposit.add(balance).subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);

                        if (!balance.equals(new Money(0))){//余额没钱，不用插入记录，全部从押金扣
                            financeBillService.insertBill(balance, condition, Payment.BALANCE, BillStatus.SUCCESS, BillType.BALANCE);
                        }
                        financeBillService.insertBill(money.subtract(balance), condition, Payment.DEPOSIT, BillStatus.SUCCESS, BillType.DEPOSIT);
                    }
                    return ResponseObj.success(financeAccount);
                case INCOME://订单收入,余额增加
                    financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
                    /**更新或插入账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, Payment.ALIPAY, BillStatus.SUCCESS, BillType.BALANCE);
                    return ResponseObj.success(financeAccount);
                default:
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易类型不存在");
            }
        }
    }

    private ResponseObj validateAccount(FinanceAccount financeAccount, TradeType tradeType, Money money, Payment payment) {
        if (!tradeType.isAdd()) {//扣除资金
            //扣除资金额大于账户总资金(罚款)
            if (money.greaterThan(new Money(financeAccount.getBalance()).add(new Money(financeAccount.getReceivedDeposit())))) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账户资金不足");
            }
            if (tradeType == TradeType.WITHDRAW) {//提现
                if (payment == Payment.BALANCE) {//余额提现
                    if (money.greaterThan(new Money(financeAccount.getBalance()))) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "余额提现，余额不足");
                    }
                }
                if (payment == Payment.DEPOSIT) {//押金提现
                    if (money.greaterThan(new Money(financeAccount.getReceivedDeposit()))) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "押金提现，押金不足");
                    }
                }
            }
        }
        return ResponseObj.success();
    }

//    private void addBalance(FinanceAccount financeAccount, Money money) {
//        financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
//        /**更新或插入账户*/
//        financeAccountDao.updateById(financeAccount);
//    }
}
