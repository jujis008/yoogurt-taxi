package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.order.dao.OrderPaymentDao;
import com.yoogurt.taxi.order.form.PayForm;
import com.yoogurt.taxi.order.form.WebhookForm;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import org.apache.commons.lang3.StringUtils;
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
     * @param orderId 订单ID
     * @return 支付记录列表
     */
    @Override
    public List<OrderPayment> getPayments(Long orderId) {
        OrderPayment probe = new OrderPayment();
        probe.setOrderId(orderId);
        return paymentDao.selectList(probe);
    }

    @Override
    public OrderPayment getPayment(String payId) {
        if (StringUtils.isBlank(payId)) return null;
        return paymentDao.selectById(payId);
    }

    @Override
    public OrderPayment addPayment(OrderPayment payment) {
        if (payment == null) return null;
        if (paymentDao.insertSelective(payment) == 1) return payment;
        return null;
    }

    @Override
    public OrderPayment modifyPayment(OrderPayment payment) {
        if (payment == null) return null;
        if (paymentDao.updateByIdSelective(payment) == 1) return payment;
        return null;
    }
}
