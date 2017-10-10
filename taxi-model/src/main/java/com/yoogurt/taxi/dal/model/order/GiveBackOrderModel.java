package com.yoogurt.taxi.dal.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GiveBackOrderModel extends DisobeyOrderModel {

	private String realGiveBackAddress;

	private Double giveBackLng;

	private Double giveBackLat;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date realGiveBackTime;

	@Override
	public String getServiceName() {
		return "giveBackService";
	}
}
