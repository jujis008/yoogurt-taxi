package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderHandoverRule;

public interface HandoverRuleService {

	/**
	 * 获取交车违约规则介绍，根据数据库中的数据动态拼接。
	 */
	String getIntroduction();

	/**
	 * 根据交车超时时间，获取违约规则，返回null表示没有违约
	 */
	OrderHandoverRule getRuleInfo(int time);

}
