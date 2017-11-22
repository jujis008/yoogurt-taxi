package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.order.dao.OrderStatisticDao;
import com.yoogurt.taxi.order.form.OrderStatisticForm;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatisticServiceImpl implements OrderStatisticService {

    @Autowired
    private OrderStatisticDao orderStatisticDao;

    /**
     * 获取司机的订单统计信息
     *
     * @param userId 用户id
     * @return 订单统计信息，如果没有统计信息，会返回null。
     */
    @Override
    public OrderStatistic getOrderStatistics(String userId) {
        if(StringUtils.isBlank(userId)) {
            return null;
        }
        return orderStatisticDao.selectById(userId);
    }

    /**
     * 记录订单统计信息
     *
     * @param form 记录信息表单信息
     */
    @Override
    public void record(OrderStatisticForm form) {
        if (form.isValid()) {
            OrderStatistic statistic = new OrderStatistic();
            BeanUtils.copyProperties(form, statistic);
            orderStatisticDao.saveStatistic(statistic);
        }
    }
}
