package com.yoogurt.taxi.dal.model.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCommentModel {

	private String driverId;

	private Long tagId;

	private Integer tagName;

	private Integer counter;

}
