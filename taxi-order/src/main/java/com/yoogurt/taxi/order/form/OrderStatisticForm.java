package com.yoogurt.taxi.order.form;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class OrderStatisticForm {

	private Long userId;

	/**
	 * 订单的变更量
	 */
	private int orderCount;

	/**
	 * 重新计算平均分
	 */
	private BigDecimal score;

	/**
	 * 违章的变更量
	 */
	private int trafficViolationCount;

	/**
	 * 违约的变更量
	 */
	private int disobeyCount;

	/**
	 * 验证此次提交的统计信息是否有效，无效的表单信息将会被直接忽略。
	 * @return true 有效，false 无效
	 */
	public boolean isValid() {
		return userId != null && userId > 0 && (orderCount > 0 || trafficViolationCount > 0 || disobeyCount > 0 || score.doubleValue() > 0);
	}

}
