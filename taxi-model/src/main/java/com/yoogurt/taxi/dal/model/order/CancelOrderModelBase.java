package com.yoogurt.taxi.dal.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CancelOrderModelBase extends BaseDisobeyOrderModel {

	private Integer responsibleParty;

	private String reason;

	private String payChannel;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date cancelTime;

	@Override
	public String getServiceName() {
		return "cancelService";
	}
}
