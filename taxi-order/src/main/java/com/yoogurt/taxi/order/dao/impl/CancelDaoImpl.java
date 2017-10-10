package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.mapper.OrderCancelInfoMapper;
import com.yoogurt.taxi.order.dao.CancelDao;
import org.springframework.stereotype.Repository;

@Repository
public class CancelDaoImpl extends BaseDao<OrderCancelInfoMapper, OrderCancelInfo> implements CancelDao {
}
