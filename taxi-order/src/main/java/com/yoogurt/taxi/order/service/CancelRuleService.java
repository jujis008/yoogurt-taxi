package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;

import java.math.BigDecimal;
import java.util.List;

public interface CancelRuleService {

	String getIntroduction();

	/**
	 * 获取匹配的取消违约规则
	 */
	OrderCancelRule getRuleInfo(long milliseconds);

	List<OrderCancelRule> getRules();

	Money calculate(OrderCancelRule rule, BigDecimal orderAmount);
}
