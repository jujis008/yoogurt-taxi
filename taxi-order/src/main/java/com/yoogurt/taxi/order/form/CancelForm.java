package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CancelForm extends OrderForm {

	@NotNull(message = "请指定责任方")
	private Integer responsibleParty;

	@NotBlank(message = "请说明取消原因")
	private String reason;

}
