package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.task.PayTask;

/**
 * 支付相关接口
 */
public interface PayService extends PaymentService {

    /**
     * 提交一个支付任务，返回任务相关信息，方便后续查询。
     *
     * @param form 支付参数
     * @return 支付任务信息
     */
    PayTask submit(PayForm form);

    /**
     * 获取支付任务执行结果。
     *
     * @param taskId 任务id
     * @return 支付对象
     */
    Payment queryResult(String taskId);

    /**
     * 获取任务信息。
     *
     * @param taskId 任务id
     * @return 任务信息
     */
    PayTask getTask(String taskId);

    /**
     * 取消支付任务。
     *
     * @param taskId 任务id
     * @return 任务信息
     */
    PayTask cancel(String taskId);

    /**
     * 任务重试
     *
     * @param taskId 任务id
     * @return 任务信息
     */
    PayTask retry(String taskId);
}
