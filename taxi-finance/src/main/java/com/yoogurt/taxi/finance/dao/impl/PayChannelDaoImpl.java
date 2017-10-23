package com.yoogurt.taxi.finance.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinancePayChannel;
import com.yoogurt.taxi.dal.mapper.FinancePayChannelMapper;
import com.yoogurt.taxi.finance.dao.PayChannelDao;
import org.springframework.stereotype.Repository;

@Repository
public class PayChannelDaoImpl extends BaseDao<FinancePayChannelMapper, FinancePayChannel> implements PayChannelDao {
}
