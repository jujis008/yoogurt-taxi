package com.yoogurt.taxi.account.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.mapper.FinanceAccountMapper;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;

public interface FinanceAccountDao extends IDao<FinanceAccountMapper,FinanceAccount> {
    Pager<FinanceAccountListModel> getListWeb(AccountListWebCondition condition);
}
