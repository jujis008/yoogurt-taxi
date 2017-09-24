package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.form.PlaceOrderForm;

public interface OrderInfoService {

	/**
	 * 接单
	 */
	OrderModel placeOrder(PlaceOrderForm orderForm);

	Pager<OrderModel> getOrderList(OrderListCondition condition);

	OrderModel getOrderInfo(Long orderId);

    OrderInfo buildOrderInfo(PlaceOrderForm orderForm);
}
