package com.yoogurt.taxi.dal.bo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WxNotify extends Notify {

	/**
	 * 微信开放平台审核通过的应用APPID
	 */
	private String appId;

	/**
	 * 微信支付分配的商户号
	 */
	private String mchId;

	/**
	 * 微信支付分配的终端设备号
	 */
	private String deviceInfo;

	/**
	 * 随机字符串，不长于32位
	 */
	private String nonceStr;

	/**
	 * 用户在商户appid下的唯一标识
	 */
	private String openId;

	/**
	 * 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
	 */
	private String isSubscribe;

	/**
	 * 交易类型：APP
	 */
	private String tradeType;

	/**
	 * 银行类型，采用字符串类型的银行标识
	 */
	private String bankType;

	/**
	 * 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY
	 */
	private String feeType;

	/**
	 * 现金支付金额订单现金支付金额，单位为【分】
	 */
	private long cashFee;

	private String cashFeeType;

	/**
	 * 代金券或立减优惠金额<=订单总金额，订单总金额-代金券或立减优惠金额=现金支付金额
	 */
	private Long couponFee;

	/**
	 * 代金券或立减优惠使用数量
	 */
	private Integer couponCount;

	/**
	 * 微信支付流水号
	 */
	private String transactionId;

	private String attach;

	/**
	 * 支付完成时间，格式为yyyyMMddHHmmss
	 */
	private String timeEnd;

	private String returnCode;

	private String returnMsg;

	private String errCode;

	private String errCodeDes;

}
