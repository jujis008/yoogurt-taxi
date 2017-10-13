package com.yoogurt.taxi.account.dao.impl;

import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.mapper.FinanceBillMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceBillDaoImpl extends BaseDao<FinanceBillMapper,FinanceBill> implements FinanceBillDao {
}
