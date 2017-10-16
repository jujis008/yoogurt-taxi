package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;
import tk.mybatis.mapper.common.Mapper;

public interface FinanceBillMapper extends Mapper<FinanceBill> {
    Page<FinanceBillListModel> getFinanceBillListApp(AccountListAppCondition condition);
}