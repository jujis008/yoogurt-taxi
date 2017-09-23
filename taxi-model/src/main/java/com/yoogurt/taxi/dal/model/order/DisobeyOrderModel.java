package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class DisobeyOrderModel extends OrderModel {

	private Long ruleId;

	private BigDecimal fineMoney;

	private Integer time;

	private String unit;

	private Boolean isDisobey;

}
