package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class HandoverForm extends OrderForm {

	@NotNull(message = "请指定租单")
	private Long orderId;

	@NotBlank(message = "请指定交车地址")
	private String realHandoverAddress;

	@NotNull(message = "请指定交车地点经纬度")
	private Double lng;

	@NotNull(message = "请指定交车地点经纬度")
	private Double lat;

}
