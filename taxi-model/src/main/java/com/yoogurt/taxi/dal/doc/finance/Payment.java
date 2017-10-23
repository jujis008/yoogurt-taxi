package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Payment extends PayParams {

	/**
	 * 支付对象 id
	 */
	private String payId;

	/**
	 * 支付凭证，用于客户端发起支付
	 */
	private Map<String, Object> credential;

	/**
	 * 支付对象生成的时间戳，精确到毫秒
	 */
	private Long created;

	/**
	 * 是否已完成支付
	 */
	private boolean paid;

	/**
	 * 支付完成的时间戳
	 */
	private Long paidTime;

	/**
	 * 支付渠道返回的交易流水号，部分渠道返回该字段为空
	 */
	private String transactionNo;

	/**
	 * 任务执行状态码
	 */
	private String statusCode;

	private String message;

}
