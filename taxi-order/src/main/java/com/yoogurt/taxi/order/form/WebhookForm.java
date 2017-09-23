package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WebhookForm {

	private String payId;

	private String orderId;

	/**
	 * 业务代码，10-租金支付，20-充值，30-违约金支付
	 */
	private Integer bizCode;

	private String channel;

	private String transactionNo;

	private Integer statusCode;

	private Integer message;

	private Map metadata;

}
