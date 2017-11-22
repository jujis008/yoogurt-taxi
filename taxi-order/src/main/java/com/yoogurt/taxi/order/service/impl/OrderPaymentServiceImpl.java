package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.order.dao.OrderPaymentDao;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class OrderPaymentServiceImpl implements OrderPaymentService {

    @Autowired
    private OrderPaymentDao paymentDao;

    /**
     * 获取订单的支付记录，按创建时间倒序排列
     *
     * @param orderId 订单ID
     * @param status  10-支付未完成，20-支付已完成
     * @return 支付记录列表
     */
    @Override
    public List<OrderPayment> getPayments(String orderId, Integer status) {
        Example example = new Example(OrderPayment.class);
        example.createCriteria().andEqualTo("orderId", orderId).andEqualTo("status", status);
        example.setOrderByClause("gmt_create DESC");
        return paymentDao.selectByExample(example);
    }

    @Override
    public OrderPayment getPayment(String payId) {
        if (StringUtils.isBlank(payId)) {
            return null;
        }
        return paymentDao.selectById(payId);
    }

    @Override
    public OrderPayment addPayment(OrderPayment payment) {
        if (payment == null) {
            return null;
        }
        if (paymentDao.insertSelective(payment) == 1) {
            return payment;
        }
        return null;
    }

    @Override
    public OrderPayment modifyPayment(OrderPayment payment) {
        if (payment == null) {
            return null;
        }
        if (paymentDao.updateByIdSelective(payment) == 1) {
            return payment;
        }
        return null;
    }

    @Override
    public boolean deletePayment(String payId) {
        if (StringUtils.isBlank(payId)) {
            return false;
        }
        return paymentDao.deleteById(payId) == 1;
    }

}
