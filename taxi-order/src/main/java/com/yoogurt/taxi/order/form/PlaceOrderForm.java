package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PlaceOrderForm {

	@NotNull(message = "请指定发布信息")
	private Long rentId;

	@NotNull(message = "请指定接单司机")
	private Long userId;

}
