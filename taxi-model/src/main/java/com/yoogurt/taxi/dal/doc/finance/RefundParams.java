package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RefundParams extends CommonParams {

	private String payId;

	/**
	 * 退款金额，单位：分
	 * 不传入此参数，表示全额退款
	 */
	private long amount;

	private Map metadata;

	/**
	 * 退款描述，最多 255 个 Unicode 字符。
	 */
	private String description;

}
