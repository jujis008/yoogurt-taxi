package com.yoogurt.taxi.account.service;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;

public interface FinanceAccountService {
    FinanceAccount get(Long userId);
    ResponseObj newAccount(Long accountNo, Money balance, Money frozenBalance, Money frozenDeposit, Money receivableDeposit, Money receivedDeposit, Long userId);

    ResponseObj updateAccount(AccountUpdateCondition condition);
}
