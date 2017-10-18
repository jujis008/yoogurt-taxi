package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Refund extends RefundParams {

	/**
	 * 退款对象  id
	 */
	private String refundId;

	/**
	 * 商户订单号，这边返回的是 payment 对象中的  orderNo 参数。
	 */
	private String orderNo;

	/**
	 * 退款创建的时间，精确到秒
	 */
	private int created;

	/**
	 * 支付渠道返回的交易流水号，部分渠道返回该字段为空
	 */
	private String transactionNo;

	/**
	 * 退款成功的时间，精确到秒
	 */
	private int succeedTime;

	/**
	 * 退款状态（目前支持三种状态: pending: 处理中; success: 成功; failed: 失败）。
	 */
	private String refundStatus;

	private String message;

}
