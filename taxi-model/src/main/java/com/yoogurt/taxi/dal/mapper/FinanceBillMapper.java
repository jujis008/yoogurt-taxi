package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.condition.account.BillListWebCondition;
import com.yoogurt.taxi.dal.condition.account.ExportBillCondition;
import com.yoogurt.taxi.dal.condition.account.WithdrawListWebCondition;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface FinanceBillMapper extends Mapper<FinanceBill> {
    Page<FinanceBillListAppModel> getFinanceBillListApp(AccountListAppCondition condition);
    Page<FinanceBillListWebModel> getFinanceBillListWeb(BillListWebCondition condition);
    Page<WithdrawBillListWebModel> getWithdrawBillListWeb(WithdrawListWebCondition condition);
    List<Map<String,Object>> getWithdrawListForExport(ExportBillCondition condition);
}