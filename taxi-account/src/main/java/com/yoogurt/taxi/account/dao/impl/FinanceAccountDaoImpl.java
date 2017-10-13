package com.yoogurt.taxi.account.dao.impl;

import com.yoogurt.taxi.account.dao.FinanceAccountDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.mapper.FinanceAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceAccountDaoImpl extends BaseDao<FinanceAccountMapper,FinanceAccount> implements FinanceAccountDao {
    /*@Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public int saveOrUpdate(FinanceAccount account) {
        return financeAccountMapper.saveOrUpdate(account);
    }*/
}
