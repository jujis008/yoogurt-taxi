package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.dal.model.order.OrderStatisticModel;
import com.yoogurt.taxi.order.form.OrderStatisticForm;

public interface OrderStatisticService {

	/**
	 * 获取司机的订单统计信息
	 * @param userId 用户id
	 * @return 订单统计信息，如果没有统计信息，会返回null。
	 */
	OrderStatistic getOrderStatistics(Long userId);

	/**
	 * 记录订单统计信息
	 * @param form 记录信息表单信息
	 */
	void record(OrderStatisticForm form);

}
