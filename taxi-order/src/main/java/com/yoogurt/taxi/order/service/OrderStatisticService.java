package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.model.order.OrderStatisticModel;
import com.yoogurt.taxi.order.form.OrderStatisticForm;

public interface OrderStatisticService {

	/**
	 * 获取司机的订单统计信息
	 */
	OrderStatisticModel getOrderStatistics(Long driverId);

	OrderStatisticModel modifyStatistics(OrderStatisticForm form);

}
