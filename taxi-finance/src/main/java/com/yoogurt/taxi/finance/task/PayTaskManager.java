package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;

/**
 * 生成支付任务，提交给MQ
 */
public interface PayTaskManager {

	public PayTask submit(PayParams params);

	/**
	 * 取消支付任务，任务状态变为EXECUTE_CANCELED
	 */
	PayTask cancel(String taskId);

	/**
	 * 获取支付任务信息
	 */
	PayTask getTask(String taskId);

	/**
	 * 获取支付任务执行结果——payment
	 */
	Payment queryResult(String taskId);

	PayTask retry(PayParams params);

}
