package com.yoogurt.taxi.dal.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Notify {

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

}
