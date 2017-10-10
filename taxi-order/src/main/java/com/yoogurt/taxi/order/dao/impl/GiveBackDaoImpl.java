package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderGiveBackInfo;
import com.yoogurt.taxi.dal.mapper.OrderGiveBackInfoMapper;
import com.yoogurt.taxi.order.dao.GiveBackDao;
import org.springframework.stereotype.Repository;

@Repository
public class GiveBackDaoImpl extends BaseDao<OrderGiveBackInfoMapper, OrderGiveBackInfo> implements GiveBackDao {
}
