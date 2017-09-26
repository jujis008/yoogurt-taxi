package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.form.PlaceOrderForm;

public interface OrderInfoService {

	/**
	 * 接单
	 */
	ResponseObj placeOrder(PlaceOrderForm orderForm);

	Pager<OrderModel> getOrderList(OrderListCondition condition);

	OrderModel getOrderInfo(Long orderId);

}
