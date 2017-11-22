package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentForm extends BaseOrderForm {

	@NotNull(message = "请指定评分")
	private Integer score;

	@Size(max = 3, message = "最多选3个评价标签")
	private Long[] tagId;

	@Size(max = 3, message = "最多选3个评价标签")
	private String[] tagName;

	@Length(max = 128, message = "备注信息最多128个字")
	private String remark;

	/**
	 * 提交评价的用户类型
	 */
	private Integer userType;
}
