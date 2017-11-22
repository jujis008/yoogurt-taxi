package com.yoogurt.taxi.pay.params;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RefundParams extends BaseCommonParams {

	/**
	 * 对应的付款对象ID
	 */
	private String payId;

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

}
