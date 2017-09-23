package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HandoverOrderModel extends DisobeyOrderModel {

	private String realHandoverAddress;

	private Date realHandoverTime;

	private Double handoverLng;

	private Double handoverLat;

}
