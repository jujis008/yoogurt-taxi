package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.order.form.PayForm;
import com.yoogurt.taxi.order.form.WebhookForm;

import java.util.List;

public interface OrderPaymentService {

    /**
     * 获取订单的支付记录
     */
    List<OrderPayment> getPayments(Long orderId);

    OrderPayment getPayment(String payId);

    OrderPayment addPayment(OrderPayment payment);

    OrderPayment modifyPayment(OrderPayment payment);

}
