package com.yoogurt.taxi.dal.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@ToString
public class WxNotify extends BaseNotify {

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

	/**
	 * 货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY
	 */
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
	 * 支付完成时间，格式为yyyyMMddHHmmss
	 */
	private String timeEnd;

	private String returnCode;

	private String returnMsg;

	private String errCode;

	private String errCodeDes;

	@Override
    public Map<String, Object> attributeMap() {
		return new HashMap<String, Object>(22){{
			put("appid", "appId");
			put("out_trade_no", "orderNo");
			put("transaction_id", "transactionNo");
			put("mch_id", "mchId");
			put("device_info", "deviceInfo");
			put("out_biz_no", "outBizNo");
			put("nonce_str", "nonceStr");
			put("result_code", "resultCode");
			put("err_code", "errCode");
			put("err_code_des", "errCodeDes");
			put("openid", "openId");
			put("is_subscribe", "isSubscribe");
			put("trade_type", "tradeType");
			put("bank_type", "bankType");
			put("fee_type", "feeType");
			put("cash_fee", "cashFee");
			put("cash_fee_type", "cashFeeType");
			put("coupon_fee", "couponFee");
			put("coupon_count", "couponCount");
			put("time_end", "timeEnd");
			put("return_code", "returnCode");
			put("return_msg", "returnMsg");
		}};
	}
}
