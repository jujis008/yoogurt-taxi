package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.form.PlaceOrderForm;

public interface OrderInfoService extends OrderBizService {

	/**
	 * 接单
	 */
	ResponseObj placeOrder(PlaceOrderForm orderForm);

	/**
	 * 分页获取订单列表
	 * @param condition 查询条件
	 * @return 订单列表
	 */
	Pager<OrderModel> getOrderList(OrderListCondition condition);

	/**
	 * 获取订单基本信息
	 * @param orderId 订单id
	 * @return 订单基本信息
	 */
	OrderInfo getOrderInfo(Long orderId);

	/**
	 * 订单详情接口
	 * @param orderId 订单id
	 * @return 订单详细信息
	 */
	OrderModel getOrderDetails(Long orderId);

	/**
	 * 修改订单状态
	 * @param orderId 订单id
	 * @param status 修改后的订单状态
	 * @return true-修改成功，false-修改失败
	 */
	boolean modifyStatus(Long orderId, OrderStatus status);
}
