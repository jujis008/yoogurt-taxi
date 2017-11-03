package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.dal.mapper.OrderPaymentMapper;
import com.yoogurt.taxi.order.dao.OrderPaymentDao;
import org.springframework.stereotype.Repository;

@Repository
public class OrderPaymentDaoImpl extends BaseDao<OrderPaymentMapper, OrderPayment> implements OrderPaymentDao {
}
