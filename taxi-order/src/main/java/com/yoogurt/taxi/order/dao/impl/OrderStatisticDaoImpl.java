package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.dal.mapper.OrderStatisticMapper;
import com.yoogurt.taxi.order.dao.OrderStatisticDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderStatisticDaoImpl extends BaseDao<OrderStatisticMapper, OrderStatistic> implements OrderStatisticDao {

    @Autowired
    private OrderStatisticMapper mapper;

    @Override
    public int saveStatistic(OrderStatistic statistic) {
        return mapper.saveStatistic(statistic);
    }
}
