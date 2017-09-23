package com.yoogurt.taxi.order.form;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderStatisticForm {

	private Long driverId;

	private Integer orderCount;

	private BigDecimal score;

	private Integer trafficViolationCount;

	private Integer disobeyCount;

}
