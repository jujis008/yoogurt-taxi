package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderAcceptInfo;
import com.yoogurt.taxi.dal.mapper.OrderAcceptInfoMapper;
import com.yoogurt.taxi.order.dao.AcceptDao;
import org.springframework.stereotype.Repository;

@Repository
public class AcceptDaoImpl extends BaseDao<OrderAcceptInfoMapper, OrderAcceptInfo> implements AcceptDao {

}
