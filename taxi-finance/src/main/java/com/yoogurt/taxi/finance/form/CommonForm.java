package com.yoogurt.taxi.finance.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public abstract class CommonForm implements Serializable {

	/**
	 * 接入支付的应用id，用于加载该应用的支付配置参数
	 */
	@NotBlank(message = "AppIdError")
	private String appId;

	/**
	 * 接口版本号
	 */
	private String version = "v0.1";

	/**
	 * 接口名称
	 */
	@NotBlank(message = "请指定接口名称")
	private String method;

	/**
	 * 发送请求的时间戳
	 */
	@NotNull(message = "请指定请求时间")
	private Long timestamp;

	/**
	 * 发起请求的客户端ip地址
	 */
	@NotBlank(message = "请指定客户端IP地址")
	private String clientIp;

}
