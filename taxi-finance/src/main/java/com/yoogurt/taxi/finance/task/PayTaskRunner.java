package com.yoogurt.taxi.finance.task;

/**
 * 执行支付任务
 */
public interface PayTaskRunner {

	/**
	 * 通过第三方交易平台，获取一个支付对象
	 * @param task 支付任务
	 */
	void run(PayTask task);

}
