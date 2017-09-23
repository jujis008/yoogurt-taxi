package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptOrderModel extends OrderModel {

	private Boolean carStatus;

	private String description;

}
