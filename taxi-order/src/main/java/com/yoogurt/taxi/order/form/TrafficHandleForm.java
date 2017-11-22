package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TrafficHandleForm extends BaseOrderForm {

	@NotNull(message = "请指定违约记录")
	private Long id;

	private boolean status;

}
