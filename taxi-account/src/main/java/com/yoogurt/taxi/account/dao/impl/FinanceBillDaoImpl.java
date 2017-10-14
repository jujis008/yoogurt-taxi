package com.yoogurt.taxi.account.dao.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.common.factory.AppPagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.mapper.FinanceBillMapper;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceBillDaoImpl extends BaseDao<FinanceBillMapper,FinanceBill> implements FinanceBillDao {
    @Autowired
    private FinanceBillMapper financeBillMapper;
    @Autowired
    private AppPagerFactory appPagerFactory;
    @Override
    public Pager<FinanceBillListModel> getFinanceBillListApp(RecordListAppCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize()).setOrderBy(" gmt_modify desc");
        Page<FinanceBillListModel> financeBillListApp = financeBillMapper.getFinanceBillListApp(condition);
        return appPagerFactory.generatePager(financeBillListApp);
    }
}
