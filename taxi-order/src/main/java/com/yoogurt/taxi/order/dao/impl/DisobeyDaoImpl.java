package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.mapper.OrderDisobeyInfoMapper;
import com.yoogurt.taxi.order.dao.DisobeyDao;
import org.springframework.stereotype.Repository;

@Repository
public class DisobeyDaoImpl extends BaseDao<OrderDisobeyInfoMapper, OrderDisobeyInfo> implements DisobeyDao {
}
