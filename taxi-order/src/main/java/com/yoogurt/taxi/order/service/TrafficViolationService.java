package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.order.form.TrafficViolationForm;

import java.util.List;

public interface TrafficViolationService {

	List getTrafficViolations(TrafficViolationListCondition condition);

	OrderTrafficViolationInfo getTrafficViolationInfo(Long id);

	OrderTrafficViolationInfo addTrafficViolation(OrderTrafficViolationInfo trafficViolation);

	OrderTrafficViolationInfo buildTrafficViolation(TrafficViolationForm form);

	/**
	 * 更改违章处理状态
	 */
	Boolean modifyStatus(int status);

	/**
	 * 判断是否可以录入违章信息，交易结束后20天内可以提交
	 */
	Boolean isAllowed(Long orderId);

}
