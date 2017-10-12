package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.mapper.OrderTrafficViolationInfoMapper;
import com.yoogurt.taxi.order.dao.TrafficViolationDao;
import org.springframework.stereotype.Repository;

@Repository
public class TrafficViolationDaoImpl extends BaseDao<OrderTrafficViolationInfoMapper, OrderTrafficViolationInfo> implements TrafficViolationDao {
}
