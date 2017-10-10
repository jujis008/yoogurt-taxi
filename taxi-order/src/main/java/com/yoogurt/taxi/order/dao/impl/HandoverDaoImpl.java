package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderHandoverInfo;
import com.yoogurt.taxi.dal.mapper.OrderHandoverInfoMapper;
import com.yoogurt.taxi.order.dao.HandoverDao;
import org.springframework.stereotype.Repository;

@Repository
public class HandoverDaoImpl extends BaseDao<OrderHandoverInfoMapper, OrderHandoverInfo> implements HandoverDao {
}
