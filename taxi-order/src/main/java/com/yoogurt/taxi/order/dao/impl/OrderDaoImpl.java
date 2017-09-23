package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.mapper.OrderInfoMapper;
import com.yoogurt.taxi.order.dao.OrderDao;

public class OrderDaoImpl extends BaseDao<OrderInfoMapper, OrderInfo> implements OrderDao{
}
