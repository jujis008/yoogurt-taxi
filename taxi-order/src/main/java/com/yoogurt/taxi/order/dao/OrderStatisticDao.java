package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.dal.mapper.OrderStatisticMapper;

public interface OrderStatisticDao extends IDao<OrderStatisticMapper, OrderStatistic> {

    int saveStatistic(OrderStatistic statistic);
}
