package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiveBackOrderModel extends DisobeyOrderModel {

	private String realGiveBackAddress;

	private Double giveBackLng;

	private Double giveBackLat;

	@Override
	public String getServiceName() {
		return "com.yoogurt.taxi.order.service.GiveBackService";
	}
}
