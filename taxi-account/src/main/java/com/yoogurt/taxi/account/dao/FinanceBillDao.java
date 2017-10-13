package com.yoogurt.taxi.account.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.mapper.FinanceBillMapper;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;

public interface FinanceBillDao extends IDao<FinanceBillMapper,FinanceBill> {
    Pager<FinanceBillListModel> getFinanceBillListApp(RecordListAppCondition condition);
}
