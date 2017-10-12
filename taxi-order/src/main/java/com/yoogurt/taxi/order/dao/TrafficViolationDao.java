package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.mapper.OrderTrafficViolationInfoMapper;

public interface TrafficViolationDao extends IDao<OrderTrafficViolationInfoMapper, OrderTrafficViolationInfo> {
}
