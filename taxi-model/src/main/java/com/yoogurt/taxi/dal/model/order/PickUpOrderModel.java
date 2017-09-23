package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickUpOrderModel extends OrderModel {

	private Boolean carStatus;

	private String description;

}
