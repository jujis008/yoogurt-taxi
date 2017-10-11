package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.model.order.OrderModel;

public interface OrderBizService {

    OrderModel info(Long orderId, Long userId);
}
