package com.yoogurt.taxi.dal.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RentInfoModel {

	private String rentId;

	private String userId;

	private String driverId;

	private String driverName;

	private String plateNumber;

	private String company;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date handoverTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
	private Date giveBackTime;

	private BigDecimal price;

	private String carThumb;

	private String avatar;

	private String address;

	private Double lng;

	private Double lat;

	private int trafficViolationCount;

	private int disobeyCount;

	private int orderCount;

	private BigDecimal score;

	private Integer status;

	private String remark;

}
