package com.yoogurt.taxi.dal.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PickUpOrderModel extends OrderModel {

	private Boolean carStatus;

	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date pickUpTime;

	@Override
	public String getServiceName() {
		return "pickUpService";
	}
}
