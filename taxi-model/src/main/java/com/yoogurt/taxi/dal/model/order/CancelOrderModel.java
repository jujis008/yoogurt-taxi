package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderModel extends DisobeyOrderModel {

	private Integer responsibleParty;

	private String reason;

	private String payChannel;

	@Override
	public String getServiceName() {
		return "cancelService";
	}
}
