package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RentListModel {

	private String platNumber;

	private Date handoverTime;

	private Date giveBackTime;

	private BigDecimal price;

	private String carThumb;

	private String address;

	private Integer orderCount;

	private Integer score;

	/**
	 * 还车时间减去交车时间，计算间隔的时长，单位：小时，向下取整。
	 */
	private Integer rentTime;

}
