package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;

import java.util.concurrent.TimeUnit;

/**
 * 生成支付任务，提交给MQ
 */
public interface PayTaskManager {

	PayTask submit(PayParams params);

	/**
	 * 取消支付任务，任务状态变为EXECUTE_CANCELED
	 */
	PayTask cancel(String taskId);

	/**
	 * 获取支付任务信息
	 */
	PayTask getTask(String taskId);

	/**
	 * 获取支付任务执行结果。
	 */
	Payment queryResult(String taskId);

	/**
	 * 获取支付任务执行结果，可设置获取超时时间。
	 */
	Payment queryResult(String taskId, long timeout, TimeUnit unit);

	/**
	 * 任务重试
	 * @param params
	 * @return
	 */
	PayTask retry(PayParams params);

}
