package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.mapper.OrderCancelInfoMapper;

public interface CancelDao extends IDao<OrderCancelInfoMapper, OrderCancelInfo> {
}
