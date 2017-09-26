package com.yoogurt.taxi.order.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.mapper.OrderInfoMapper;
import com.yoogurt.taxi.dal.model.order.OrderModel;

import java.util.Date;

public interface OrderDao extends IDao<OrderInfoMapper, OrderInfo> {

    Page<OrderModel> getOrderList(OrderListCondition condition);
}
