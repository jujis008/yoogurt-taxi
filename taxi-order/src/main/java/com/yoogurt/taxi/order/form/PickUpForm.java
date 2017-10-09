package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PickUpForm extends OrderForm {

	@NotNull(message = "请指定租单")
	private Long orderId;

	@NotNull(message = "请指定车辆状态")
	private Boolean carStatus;

	/**
	 * 如果车辆状态不正常，可以上传车辆图片
	 */
	@Size(max = 4, message = "最多只能上传4张图片")
	private String[] pictures;
}
