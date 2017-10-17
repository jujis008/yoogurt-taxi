package com.yoogurt.taxi.account.dao.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.account.dao.FinanceAccountDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.condition.account.AccountListWebCondition;
import com.yoogurt.taxi.dal.mapper.FinanceAccountMapper;
import com.yoogurt.taxi.dal.model.account.FinanceAccountListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceAccountDaoImpl extends BaseDao<FinanceAccountMapper,FinanceAccount> implements FinanceAccountDao {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Autowired
    private WebPagerFactory webPagerFactory;
    @Override
    public Pager<FinanceAccountListModel> getListWeb(AccountListWebCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize()).setOrderBy(" gmt_create desc");
        Page<FinanceAccountListModel> listWeb = financeAccountMapper.getListWeb(condition);
        return webPagerFactory.generatePager(listWeb);
    }
    /*@Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public int saveOrUpdate(FinanceAccount account) {
        return financeAccountMapper.saveOrUpdate(account);
    }*/
}
