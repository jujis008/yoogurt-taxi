package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.mapper.OrderInfoMapper;

public interface OrderDao extends IDao<OrderInfoMapper, OrderInfo> {
}
