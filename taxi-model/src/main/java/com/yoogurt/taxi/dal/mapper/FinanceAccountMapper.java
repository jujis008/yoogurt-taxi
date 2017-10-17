package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;
import tk.mybatis.mapper.common.Mapper;

public interface FinanceAccountMapper extends Mapper<FinanceAccount> {

    Page<FinanceAccountListModel> getListWeb(AccountListWebCondition condition);

//    int saveOrUpdate(FinanceAccount account);
}