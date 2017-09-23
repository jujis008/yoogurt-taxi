package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderCancelRule;

public interface CancelRuleService {

	String getIntroduction();

	/**
	 * 获取匹配的取消违约规则
	 */
	OrderCancelRule getRuleInfo(int time);

}
