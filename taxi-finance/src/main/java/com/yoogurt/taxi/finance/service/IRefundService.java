package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.doc.finance.Refund;
import com.yoogurt.taxi.dal.doc.finance.RefundParams;
import com.yoogurt.taxi.finance.task.RefundTask;

public interface IRefundService {

	RefundTask submitRefundTask(RefundParams params);

	Refund queryResult(String taskId);

	RefundTask getTask(String taskId);

	RefundTask cancelRefundTask(String taskId);

	/**
	 * 根据id获取退款对象
	 */
	Refund getRefund(String refundId);

}
