package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.order.dao.OrderPaymentDao;
import com.yoogurt.taxi.order.form.PayForm;
import com.yoogurt.taxi.order.form.WebhookForm;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderPaymentServiceImpl implements OrderPaymentService {

    @Autowired
    private OrderPaymentDao paymentDao;

    /**
     * 获取订单的支付记录
     *
     * @param orderId
     */
    @Override
    public List<OrderPayment> getPayments(Long orderId) {
        OrderPayment probe = new OrderPayment();
        probe.setOrderId(orderId);
        return paymentDao.selectList(probe);
    }

    @Override
    public OrderPayment getPayment(String payId) {
        return null;
    }

    @Override
    public Boolean addPayment(OrderPayment payment) {
        return null;
    }

    @Override
    public OrderPayment buildPayment(PayForm form) {
        return null;
    }

    /**
     * 处理订单支付回调事件
     *
     * @param form
     */
    @Override
    public OrderPayment callback(WebhookForm form) {
        return null;
    }

    /**
     * 修改支付信息
     *
     * @param payment
     */
    @Override
    public Boolean modifyPayment(OrderPayment payment) {
        return null;
    }
}
