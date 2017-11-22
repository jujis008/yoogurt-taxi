package com.yoogurt.taxi.pay.params;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class PayParams extends BaseCommonParams {

	/**
	 * 商户订单号，不可重复
	 */
	@NotBlank(message = "请指定订单号")
	private String orderNo;

	/**
	 * 支付金额，单位：分
	 */
	@NotNull(message = "请指定订单总金额")
	private Long amount;

	/**
	 * 支付渠道
	 */
	@NotBlank(message = "请指定支付渠道")
	private String channel;

	/**
	 * 主题信息，最长为 32 个 Unicode 字符
	 */
	@NotBlank(message = "请指定商品标题")
	private String subject;

	/**
	 * 主体信息，最长为 128 个 Unicode 字符
	 */
	private String body;

	/**
	 * 附加参数，渠道返回
	 */
	private Map<String, Object> extras;

	/**
	 * 元数据，用户可定制。
	 * 最多可以拥有 10 个键值对，数据总长度在 1000 个 Unicode 字符以内
	 */
	private Map<String, Object> metadata;

	/**
	 * 订单附加说明，最多 255 个 Unicode 字符。
	 */
	private String description;

}
