package com.yoogurt.taxi.finance.bo.wx;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p class="detail">
 * 功能：微信支付参数设置的Bean，请勿随意更改！
 * </p>
 * @version V1.0
 * @author liuwh
 */
@Getter
@Setter
@Builder
public final class PrePayInfo implements Serializable {

	/** 申请微信支付的AppID */
	private String appid;

	/** 对订单的描述 */
	private String body;

	/** 商户id，可以在支付申请成功的通知邮件中看到，用于发起预支付接口 */
	private String mch_id;

	/** 随机字符串，不长于32位 */
	private String nonce_str;

	/** 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。 */
	private String notify_url;

	/** trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。 */
	private String openid;

	/** 商户生成的订单号 */
	private String out_trade_no;

	/** trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。 */
	private String product_id;

	/** 支付发起的ip地址 */
	private String spbill_create_ip;

	/** 订单总金额 */
	private Long total_fee;

	/** 交易类型，取值如下：JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付 */
	private String trade_type;

	/** 商户支付密钥，设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 */
	private String key;

	/** 签名类型，默认为MD5，支持HMAC-SHA256和MD5 */
	private String sign_type;

	/** 根据微信提供的算法生成一个签名 */
	private String sign;

	/** key*/
	private String prepayId;

	/** 订单下单时间撮、判断订单支付是否超时*/
	private Long orderDate;

}
