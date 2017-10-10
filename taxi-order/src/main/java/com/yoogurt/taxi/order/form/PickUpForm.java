package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PickUpForm extends OrderForm {

	@NotNull(message = "请指定租单")
	private Long orderId;

	/**
	 * false-异常，true-正常
	 */
	@NotNull(message = "请指定车辆状态")
	private Boolean carStatus;

	/**
	 * 异常描述
	 */
	@Length(max = 200, message = "描述内容不能超过200个字")
	private String description;

	/**
	 * 如果车辆状态不正常，可以上传车辆图片
	 */
	@Size(max = 4, message = "最多只能上传4张图片")
	private String[] pictures;
}
