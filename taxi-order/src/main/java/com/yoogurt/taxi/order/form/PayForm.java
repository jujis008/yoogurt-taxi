package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class PayForm extends BaseOrderForm {

	@NotBlank(message = "请指定支付方式")
	private String payChannel;

}
