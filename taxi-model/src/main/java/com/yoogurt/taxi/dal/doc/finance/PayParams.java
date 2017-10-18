package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PayParams extends CommonParams {

	/**
	 * 商户订单号，不可重复
	 */
	private String orderNo;

	/**
	 * 支付金额，单位：分
	 */
	private Long amount;

	private String channel;

	private String subject;

	private String body;

	private Map extras;

	private Map metadata;

	/**
	 * 订单附加说明，最多 255 个 Unicode 字符。
	 */
	private String description;

}
