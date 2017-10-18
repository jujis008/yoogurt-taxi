package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.Payment;

/**
 * 从MQ中取出支付任务，进行执行
 */
public interface PayTaskRunner {

	/**
	 * 通过第三方交易平台，获取一个支付对象
	 */
	Payment run(PayTask task);

}
