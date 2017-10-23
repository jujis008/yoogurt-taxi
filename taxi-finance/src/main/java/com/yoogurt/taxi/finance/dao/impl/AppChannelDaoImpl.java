package com.yoogurt.taxi.finance.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceAppChannel;
import com.yoogurt.taxi.dal.mapper.FinanceAppChannelMapper;
import com.yoogurt.taxi.finance.dao.AppChannelDao;
import org.springframework.stereotype.Repository;

@Repository
public class AppChannelDaoImpl extends BaseDao<FinanceAppChannelMapper, FinanceAppChannel> implements AppChannelDao {
}
