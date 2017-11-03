package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;

import java.math.BigDecimal;

public interface HandoverRuleService {

	/**
	 * 获取交车违约规则介绍，根据数据库中的数据动态拼接。
	 */
	String getIntroduction();

	/**
	 * 根据交车超时时间，获取违约规则，返回null表示没有违约
     * @param milliseconds
     */
	OrderHandoverRule getRuleInfo(long milliseconds);

	/**
	 * 获取一条交车违约规则。
	 * 一个时刻只会允许一条交车违约规则生效。
	 */
	OrderHandoverRule getRuleInfo();

	/**
	 * 计算罚款金额。
	 */
	Money calculate(OrderHandoverRule rule, int time, BigDecimal limitAmount);
}
