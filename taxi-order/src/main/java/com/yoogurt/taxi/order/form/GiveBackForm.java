package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class GiveBackForm extends OrderForm{

	@NotBlank(message = "请指定还车地址")
	private String realGiveBackAddress;

	@NotNull(message = "请指定还车地点经纬度")
	private Double lng;

	@NotNull(message = "请指定还车地点经纬度")
	private Double lat;

}
