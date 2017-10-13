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
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public ResponseObj createAccount(Long accountNo, Money receivableDeposit, Long userId) {
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAccountNo(accountNo);
        financeAccount.setBalance(new BigDecimal(0));
        financeAccount.setFrozenBalance(new BigDecimal(0));
        financeAccount.setFrozenDeposit(new BigDecimal(0));
        financeAccount.setReceivableDeposit(Constants.receivableDeposit);
        financeAccount.setReceivedDeposit(new BigDecimal(0));
        financeAccount.setUserId(userId);
        financeAccountDao.insert(financeAccount);//创建默认资金数账户
        return ResponseObj.success(financeAccount);
    }

    /**
     * 更新账户，具体功能有：1.充值（提现申请），2.提现（回调，即将冻结金额扣除，账户不动）
     * 3.罚款，4.补偿，5.订单收入
     * @param condition
     * @return
     */
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
                //TODO
                createAccount(RandomUtils.getPrimaryKey(), new Money(0), userId);
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
                    financeBillService.insertBill(money, condition, payment, BillStatus.PENDING);
                    return ResponseObj.success(financeAccount);
                case CHARGE://充值(回调时使用)
                    if (condition.getDestinationType() != DestinationType.DEPOSIT) {//目前只支持押金充值
                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "目前只支持押金充值");
                    }
                    financeAccount.setReceivedDeposit(new Money(financeAccount.getReceivedDeposit()).add(money).getAmount());
                    /**更新或插入账户*/
                    financeAccountDao.updateById(financeAccount);
                    FinanceBill financeBill = financeBillService.get(condition.getBillId());
                    if (financeBill == null) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"充值记录不存在");
                    }
                    if (!financeBill.getAmount().equals(money.getAmount())) {
                        return ResponseObj.fail(StatusCode.BIZ_FAILED,"充值记录异常");
                    }
                    financeBill.setTransactionNo(condition.getTransactionNo());
                    financeBill.setStatus(BillStatus.SUCCESS.getCode());
                    /**更新账单状态*/
                    financeBillService.save(financeBill);

                    /**3.插入账单记录*/
                    FinanceRecord financeRecord = new FinanceRecord(financeBill.getId(), financeBill.getBillNo(), BillStatus.SUCCESS.getCode(), null);
                    financeRecordService.save(financeRecord);
                    return ResponseObj.success(financeAccount);
                case FINE_IN://补偿,余额增加
                    this.addBalance(financeAccount, money);
                    /**更新或插入账户*/
                    financeAccountDao.updateById(financeAccount);
                    financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS);
                    return ResponseObj.success(financeAccount);
                case FINE_OUT://罚款
                    Money balance = new Money(financeAccount.getBalance());//余额
                    Money deposit = new Money(financeAccount.getReceivedDeposit());//剩余押金
                    if (!money.greaterThan(balance)) {//余额充足，从余额里扣
                        financeAccount.setBalance(balance.subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);

                        financeBillService.insertBill(money, condition, Payment.BALANCE, BillStatus.SUCCESS);
                    } else {
                        financeAccount.setBalance(new Money(0).getAmount());//余额扣光
                        financeAccount.setReceivedDeposit(deposit.add(balance).subtract(money).getAmount());
                        financeAccountDao.updateById(financeAccount);

                        financeBillService.insertBill(balance, condition, Payment.BALANCE, BillStatus.SUCCESS);
                        financeBillService.insertBill(money.subtract(balance), condition, Payment.DEPOSIT, BillStatus.SUCCESS);
                    }
                    return ResponseObj.success(financeAccount);
                case INCOME://订单收入,余额增加
                    this.addBalance(financeAccount, money);
                    financeBillService.insertBill(money, condition, Payment.ALIPAY, BillStatus.SUCCESS);
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
        financeAccountDao.updateById(financeAccount);
    }
}
