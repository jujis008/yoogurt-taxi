package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class TrafficViolationForm extends BaseOrderForm {

	@NotNull(message = "请指定违章类型")
	private Integer type;

	@NotNull(message = "请指定违章时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date happenTime;

	@NotBlank(message = "请指定违章地点")
	private String address;

	@NotBlank(message = "请填写违章描述")
	private String description;

}
