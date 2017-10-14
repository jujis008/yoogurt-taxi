package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.OrderStatistic;
import tk.mybatis.mapper.common.Mapper;

public interface OrderStatisticMapper extends Mapper<OrderStatistic> {

    int saveStatistic(OrderStatistic statistic);
}