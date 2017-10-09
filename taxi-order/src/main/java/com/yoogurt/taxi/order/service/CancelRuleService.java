package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderCancelRule;

import java.util.List;

public interface CancelRuleService {

	String getIntroduction();

	/**
	 * 获取匹配的取消违约规则
	 */
	OrderCancelRule getRuleInfo(int time, String unit);

	List<OrderCancelRule> getRules();

}
