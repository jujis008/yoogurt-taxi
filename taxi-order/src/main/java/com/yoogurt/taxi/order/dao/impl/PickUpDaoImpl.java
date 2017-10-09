package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderPickUpInfo;
import com.yoogurt.taxi.dal.mapper.OrderPickUpInfoMapper;
import com.yoogurt.taxi.order.dao.PickUpDao;
import org.springframework.stereotype.Repository;

@Repository
public class PickUpDaoImpl extends BaseDao<OrderPickUpInfoMapper, OrderPickUpInfo> implements PickUpDao {
}
