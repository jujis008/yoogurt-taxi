package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.order.form.PayForm;
import com.yoogurt.taxi.order.form.WebhookForm;

import java.util.List;

public interface OrderPaymentService {

	/**
	 * 获取订单的支付记录
	 */
	List getPayments(Long orderId);

	OrderPayment getPayment(String payId);

	Boolean addPayment(OrderPayment payment);

	OrderPayment buildPayment(PayForm form);

	/**
	 * 处理订单支付回调事件
	 */
	OrderPayment callback(WebhookForm form);

	/**
	 * 修改支付信息
	 */
	Boolean modifyPayment(OrderPayment payment);

}
