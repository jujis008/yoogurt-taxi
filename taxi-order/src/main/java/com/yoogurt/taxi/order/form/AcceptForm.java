package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AcceptForm extends OrderForm{

	@NotNull(message = "请指定车辆状态")
	private Boolean carStatus;

}
