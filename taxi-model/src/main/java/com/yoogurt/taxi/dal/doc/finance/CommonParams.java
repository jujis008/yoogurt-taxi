package com.yoogurt.taxi.dal.doc.finance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonParams {

	/**
	 * 接入支付的应用id，用于加载该应用的支付配置参数
	 */
	private String appId;

	/**
	 * 接口版本号
	 */
	private String version;

	/**
	 * 接口名称。例如：gogen.nirvana.pay
	 * 便于以后集成API网关
	 */
	private String method;

	/**
	 * 发送请求的时间戳
	 */
	private long timestamp;

	/**
	 * 发起请求的客户端ip地址
	 */
	private String clientIp;

}
