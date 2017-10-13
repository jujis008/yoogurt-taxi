package com.yoogurt.taxi.account.dao.impl;

import com.yoogurt.taxi.account.dao.FinanceRecordDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.mapper.FinanceRecordMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceRecordDaoImpl extends BaseDao<FinanceRecordMapper,FinanceRecord> implements FinanceRecordDao {
}
