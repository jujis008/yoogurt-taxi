package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RentPOIModel {

	private Long rentId;

	private Long userId;

	private Long driverId;

	private String driverName;

	private String platNumber;

	private Date handoverTime;

	private Date giveBackTime;

	private BigDecimal price;

	private String carThumb;

	private String avatar;

	private String address;

	private Double lng;

	private Double lat;

	private Integer orderCount;

	private Integer score;

	private Integer status;

}
