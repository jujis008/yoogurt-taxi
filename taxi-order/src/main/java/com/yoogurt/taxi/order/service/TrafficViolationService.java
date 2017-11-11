package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.dal.enums.TrafficStatus;
import com.yoogurt.taxi.order.form.TrafficViolationForm;

public interface TrafficViolationService {

	Pager<OrderTrafficViolationInfo> getTrafficViolations(TrafficViolationListCondition condition);

	OrderTrafficViolationInfo getTrafficViolationInfo(Long id);

	OrderTrafficViolationInfo addTrafficViolation(OrderTrafficViolationInfo trafficViolation);

	ResponseObj buildTrafficViolation(TrafficViolationForm form);

	/**
	 * 更改违章处理状态
	 */
	OrderTrafficViolationInfo modifyStatus(Long id, TrafficStatus status);

	/**
	 * 判断是否可以录入违章信息，交易结束后20天内可以提交
	 */
	ResponseObj isAllowed(String orderId, String userId);

}
