package com.yoogurt.taxi.account.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.condition.account.BillListWebCondition;
import com.yoogurt.taxi.dal.condition.account.ExportBillCondition;
import com.yoogurt.taxi.dal.condition.account.WithdrawListWebCondition;
import com.yoogurt.taxi.dal.mapper.FinanceBillMapper;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;

import java.util.List;
import java.util.Map;

public interface FinanceBillDao extends IDao<FinanceBillMapper,FinanceBill> {
    BasePager<FinanceBillListAppModel> getFinanceBillListApp(AccountListAppCondition condition);
    BasePager<FinanceBillListWebModel> getFinanceBillListWeb(BillListWebCondition condition);
    BasePager<WithdrawBillListWebModel> getWithdrawBillListWeb(WithdrawListWebCondition condition);
    List<Map<String,Object>> getWithdrawListForExport(ExportBillCondition condition);
}
