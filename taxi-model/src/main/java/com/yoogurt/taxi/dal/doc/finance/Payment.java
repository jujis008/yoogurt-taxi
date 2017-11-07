package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class Payment implements Serializable {

	/**
	 * 支付对象 id
	 */
	@Id
	private String payId;

	/**
	 * 接入支付的应用id，用于加载该应用的支付配置参数
	 */
	private String appId;

	/**
	 * 接口版本号
	 */
	private String version;

	/**
	 * 接口名称
	 */
	private String method;

	/**
	 * 发送请求的时间戳
	 */
	private Long timestamp;

	/**
	 * 发起请求的客户端ip地址
	 */
	private String clientIp;

	/**
	 * 商户订单号，不可重复
	 */
	private String orderNo;

	/**
	 * 支付金额，单位：分
	 */
	private Long amount;

	/**
	 * 支付渠道
	 */
	private String channel;

	/**
	 * 主题信息
	 */
	private String subject;

	/**
	 * 主体信息
	 */
	private String body;

	/**
	 * 订单附加说明，最多 255 个 Unicode 字符。
	 */
	private String description;

	/**
	 * 附加参数，渠道返回
	 */
	private Map<String, Object> extras;

	/**
	 * 元数据，用户可定制
	 */
	private Map<String, Object> metadata;


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
	 * 实际支付金额
	 */
	private Long paidAmount;

	/**
	 * 支付渠道返回的交易流水号，部分渠道返回该字段为空
	 */
	private String transactionNo;

	/**
	 * 任务执行状态码
	 */
	private String statusCode;

	/**
	 * 提示消息
	 */
	private String message;

	public Payment() {

	}

	public Payment(String payId) {
		this();
		this.payId = payId;
	}
}
