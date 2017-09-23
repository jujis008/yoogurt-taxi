package com.yoogurt.taxi.order.form;

public class DisobeyForm extends OrderForm {

	/**
	 * 10-正式司机交车超时，20-代理司机还车超时，30-司机取消(无责取消不记录违约)
	 */

	private Integer type;

}
