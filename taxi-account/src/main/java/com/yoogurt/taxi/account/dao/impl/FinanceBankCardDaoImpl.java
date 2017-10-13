package com.yoogurt.taxi.account.dao.impl;

import com.yoogurt.taxi.account.dao.FinanceBankCardDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceBankCard;
import com.yoogurt.taxi.dal.mapper.FinanceBankCardMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceBankCardDaoImpl extends BaseDao<FinanceBankCardMapper,FinanceBankCard> implements FinanceBankCardDao {
}
