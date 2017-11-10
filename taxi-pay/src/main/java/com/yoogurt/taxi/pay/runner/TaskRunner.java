package com.yoogurt.taxi.pay.runner;

/**
 * 执行支付任务
 */
public interface TaskRunner<T> {

	/**
	 * 通过第三方交易平台，执行特定的任务
	 * @param task 任务信息
	 */
	void run(T task);


	T retry(T task);
}
