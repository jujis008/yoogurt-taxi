package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.Refund;
import com.yoogurt.taxi.dal.doc.finance.RefundParams;

public interface RefundTaskManager {

	RefundTask submit(RefundParams params);

	/**
	 * 取消支付任务，任务状态变为EXECUTE_CANCELED
	 */
	RefundTask cancel(String taskId);

	/**
	 * 获取支付任务信息
	 */
	RefundTask getTask(String taskId);

	/**
	 * 获取支付任务执行结果——payment
	 */
	Refund queryResult(String taskId);

	RefundTask retry(RefundParams params);

}
