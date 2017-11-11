package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderPayment;

import java.util.List;

public interface OrderPaymentService {

    /**
     * 获取订单的支付记录
     */
    List<OrderPayment> getPayments(String orderId, Integer status);

    OrderPayment getPayment(String payId);

    OrderPayment addPayment(OrderPayment payment);

    OrderPayment modifyPayment(OrderPayment payment);

    boolean deletePayment(String payId);
}
