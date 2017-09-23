package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatisticModel {

	/**
	 * 司机id
	 */
	private Long driverId;

	/**
	 * 交易量
	 */
	private Integer orderCount;

	private Integer score;

	/**
	 * 违章数量
	 */
	private Integer trafficViolationCount;

	/**
	 * 违约数量
	 */
	private Integer disobeyCount;

}
