package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.task.PayTask;

public interface IPayService {

	/**
	 * 提交一个支付任务，返回任务相关信息，方便后续查询。
	 */
	PayTask submitPayTask(PayParams params);

	/**
	 * 获取支付任务执行结果——payment。
	 * 可设置获取超时时间。
	 */
	Payment queryResult(String taskId);

	/**
	 * 获取任务信息
	 */
	PayTask getTask(String taskId);

	/**
	 * 取消支付任务
	 */
	PayTask cancelPayTask(String taskId);

	/**
	 * 根据id获取支付对象
	 */
	Payment getPayment(String payId);

}
