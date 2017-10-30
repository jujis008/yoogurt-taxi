package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class Refund implements Serializable {

	/**
	 * 退款对象  id
	 */
	private String refundId;

	/**
	 * 支付对象的ID
	 */
	private String payId;

	/**
	 * 接入支付的应用id，用于加载该应用的支付配置参数
	 */
	private String appId;

	/**
	 * 接口版本号
	 */
	private String version = "v0.1";

	/**
	 * 接口名称
	 */
	private String method = "yoogurt.taxi.finance.refund";

	/**
	 * 发送请求的时间戳
	 */
	private Long timestamp;

	/**
	 * 发起请求的客户端ip地址
	 */
	private String clientIp;

	/**
	 * 退款金额，单位：分
	 * 不传入此参数，表示全额退款
	 */
	private Long amount;

	/**
	 * 元数据，用户可定制
	 */
	private Map<String, Object> metadata;

	/**
	 * 退款描述，最多 255 个 Unicode 字符。
	 */
	private String description;

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
