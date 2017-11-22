package com.yoogurt.taxi.dal.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
@ToString
public abstract class BaseNotify implements Serializable {

	/**
	 * 支付渠道，
	 * @see com.yoogurt.taxi.dal.enums.PayChannel
	 */
	private String channel;

	/**
	 * 回调的时间戳
	 */
	private long notifyTimestamp;

	/**
	 * 支付时间
	 */
	private long paidTimestamp;

	/**
	 * 编码方式
	 */
	private String charset;

	/**
	 * 签名方式
	 */
	private String signType;

	/**
	 * 第三方平台返回的签名
	 */
	private String sign;

	/**
	 * 商户订单号
	 */
	private String orderNo;

	/**
	 * 本次交易支付的订单金额，单位为人民币（元）
	 */
	private long amount;

	/**
	 * 第三方平台支付流水号
	 */
	private String transactionNo;

	/**
	 * 请求元数据，支付的时候以JSON格式传入，第三方平台原样返回。
	 * 一定要将发起支付的payId带过去。
	 */
	private Map metadata;


	abstract Map<String, Object> attributeMap();
}
