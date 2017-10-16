package com.yoogurt.taxi.account.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.condition.account.BillListWebCondition;
import com.yoogurt.taxi.dal.condition.account.WithdrawListWebCondition;
import com.yoogurt.taxi.dal.mapper.FinanceBillMapper;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;

public interface FinanceBillDao extends IDao<FinanceBillMapper,FinanceBill> {
    Pager<FinanceBillListAppModel> getFinanceBillListApp(AccountListAppCondition condition);
    Pager<FinanceBillListWebModel> getFinanceBillListWeb(BillListWebCondition condition);
    Pager<WithdrawBillListWebModel> getWithdrawBillListWeb(WithdrawListWebCondition condition);
}
