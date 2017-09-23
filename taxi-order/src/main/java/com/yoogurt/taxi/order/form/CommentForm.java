package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentForm extends OrderForm {

	@NotNull(message = "请指定评价的司机")
	private Long driverId;

	@NotNull(message = "请指定评分")
	private Integer score;

	private String tagId;

	private String tagName;

	private String remark;

}
