package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CancelForm extends BaseOrderForm {

	/**
	 * 后台客服选择。
	 * 如果是app发起取消，责任方为发起人。
	 */
	private Integer responsibleParty;

	/**
	 * 后台客服输入
	 */
	private String reason = "订单取消";

	/**
	 * 扣款渠道，默认钱包。
	 */
	private String payChannel = "wallet";

	/**
	 * 用于后台客服输入
	 */
	private BigDecimal fineMoney = new BigDecimal(0);

	/**
	 * 取消者的身份类型
	 */
	private Integer userType;

	/**
	 * 是否来源内部系统
	 */
	private boolean internal;

	/**
	 * 取消订单的请求是否来源于手机客户端
	 */
	private boolean fromApp;
}
