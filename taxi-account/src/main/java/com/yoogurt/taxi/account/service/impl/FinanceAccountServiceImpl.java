package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceAccountDao;
import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.account.dao.FinanceRecordDao;
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
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ResponseObj newAccount(Long accountNo, Money balance, Money frozenBalance, Money frozenDeposit, Money receivableDeposit, Money receivedDeposit, Long userId) {
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAccountNo(accountNo);
        financeAccount.setBalance(balance.getAmount());
        financeAccount.setFrozenBalance(frozenBalance.getAmount());
        financeAccount.setFrozenDeposit(frozenDeposit.getAmount());
        financeAccount.setReceivableDeposit(receivableDeposit.getAmount());
        financeAccount.setReceivedDeposit(receivedDeposit.getAmount());
        financeAccount.setUserId(userId);
        financeAccountDao.insert(financeAccount);//创建默认资金数账户
        return ResponseObj.success(financeAccount);
    }

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
        UserInfo userInfo = userInfoRestResult.getBody();
        synchronized (userId.toString().intern()) {
            FinanceAccount financeAccount = financeAccountDao.selectById(userId);
            BillType billType = condition.getBillType();
            if (financeAccount == null) {//账户不存在
                //创建账户
                newAccount(RandomUtils.getPrimaryKey(), new Money(0), new Money(0), new Money(0), new Money(0), new Money(0), userId);
            }
            Payment payment = condition.getPayment();
            //判断账户的有效性
            ResponseObj responseObj = this.validateAccount(financeAccount, billType, money, payment);
            if (!responseObj.isSuccess()) {
                return responseObj;
            }
            switch (billType) {
                case WITHDRAW://提现(提现申请时，提现回调另做)
                    if (payment == Payment.BALANCE) {//余额提现
                        Money balance = new Money(financeAccount.getBalance());
                        Money frozenBalance = new Money(financeAccount.getFrozenBalance());
                        financeAccount.setFrozenBalance(frozenBalance.add(money).getAmount());
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                    }
                    if (payment == Payment.DEPOSIT) {
                        Money receivedDeposit = new Money(financeAccount.getReceivedDeposit());
                        Money frozenDeposit = new Money(financeAccount.getFrozenDeposit());
                        financeAccount.setFrozenDeposit(frozenDeposit.add(money).getAmount());
                        financeAccount.setReceivedDeposit(receivedDeposit.subtract(money).getAmount());
                    }
                    /**1.更新账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, payment, userInfo, BillStatus.PENDING);
                    return ResponseObj.success(financeAccount);
                case CHARGE://充值(回调时使用)
                    if (condition.getDestinationType() != DestinationType.DEPOSIT) {//目前只支持押金充值
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "目前只支持押金充值");
                    }
                    if (financeAccount == null) {
                        financeAccount = new FinanceAccount();
                        financeAccount.setAccountNo(RandomUtils.getPrimaryKey());
                        financeAccount.setBalance(new Money(0).getAmount());
                        financeAccount.setFrozenBalance(new Money(0).getAmount());
                        financeAccount.setFrozenDeposit(new Money(0).getAmount());
                        financeAccount.setReceivableDeposit(Constants.receivableDeposit);
                        financeAccount.setReceivedDeposit(money.getAmount());
                        financeAccount.setUserId(userId);
                    } else {
                        financeAccount.setReceivedDeposit(new Money(financeAccount.getReceivedDeposit()).add(money).getAmount());
                    }
                    /**更新或插入账户*/
                    financeAccountDao.saveOrUpdate(financeAccount);
                    FinanceBill financeBill1 = financeBillService.get(condition.getBillId());
                    financeBill1.setTransactionNo(condition.getTransactionNo());
                    financeBill1.setStatus(BillStatus.SUCCESS.getCode());
                    /**更新账单状态*/
                    financeBillService.save(financeBill1);

                    /**3.插入账单记录*/
                    FinanceRecord financeRecord = new FinanceRecord(financeBill1.getId(), financeBill1.getBillNo(), BillStatus.SUCCESS.getCode(), null);
                    financeRecordService.save(financeRecord);
                    return ResponseObj.success(financeAccount);
                case FINE_IN://补偿,余额增加
                    this.addBalance(financeAccount, money);
                    /**更新或插入账户*/
                    financeAccountDao.saveOrUpdate(financeAccount);
                    financeBillService.insertBill(money, condition, Payment.BALANCE, userInfo, BillStatus.SUCCESS);
                    return ResponseObj.success(financeAccount);
                case FINE_OUT://罚款
                    Money balance = new Money(financeAccount.getBalance());//余额
                    Money deposit = new Money(financeAccount.getReceivedDeposit());//剩余押金
                    if (!money.greaterThan(balance)) {//余额充足，从余额里扣
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);

                        financeBillService.insertBill(money, condition, Payment.BALANCE, userInfo, BillStatus.SUCCESS);
                    } else {
                        financeAccount.setBalance(new Money(0).getAmount());//余额扣光
                        financeAccount.setReceivedDeposit(deposit.add(balance).subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);

                        financeBillService.insertBill(balance, condition, Payment.BALANCE, userInfo, BillStatus.SUCCESS);
                        financeBillService.insertBill(money.subtract(balance), condition, Payment.DEPOSIT, userInfo, BillStatus.SUCCESS);
                    }
                    return ResponseObj.success(financeAccount);
                case INCOME://订单收入,余额增加
                    this.addBalance(financeAccount, money);
                    financeBillService.insertBill(money, condition, Payment.ALIPAY, userInfo, BillStatus.SUCCESS);
                    return ResponseObj.success(financeAccount);
                default:
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易类型不存在");
            }
        }
    }

    private ResponseObj validateAccount(FinanceAccount financeAccount, BillType billType, Money money, Payment payment) {
        if (!billType.isAdd()) {//扣除资金
            //扣除资金额大于账户总资金(罚款)
            if (money.greaterThan(new Money(financeAccount.getBalance()).add(new Money(financeAccount.getReceivedDeposit())))) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "账户资金不足");
            }
            if (billType == BillType.WITHDRAW) {//提现
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

    private void addBalance(FinanceAccount financeAccount, Money money) {
        financeAccount.setBalance(new Money(financeAccount.getBalance()).add(money).getAmount());
        /**更新或插入账户*/
        financeAccountDao.saveOrUpdate(financeAccount);
    }
}
