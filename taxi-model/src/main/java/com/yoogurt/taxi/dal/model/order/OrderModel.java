package com.yoogurt.taxi.dal.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OrderModel {

	private String orderId;

	private String rentId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date handoverTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date giveBackTime;

	private String company;

	private String address;

	private Double lng;

	private Double lat;

	private BigDecimal price;

	private String agentUserId;

	private String agentDriverId;

	private String agentDriverName;

	private String agentDriverPhone;

	private Long carId;

	private String plateNumber;

	private String officialUserId;

	private String officialDriverId;

	private String officialDriverName;

	private String officialDriverPhone;

	/**
	 * 订单总金额
	 */
	private BigDecimal amount;

	/**
	 * 接单时间
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date orderTime;

	private Integer status;

	private Boolean isCommented;

	private Boolean isPaid;

	public String getServiceName() {
		return "orderInfoService";
	}
}
