package com.yoogurt.taxi.pay.service;

import com.yoogurt.taxi.pay.doc.Payment;
import com.yoogurt.taxi.pay.params.PayParams;
import com.yoogurt.taxi.pay.doc.PayTask;

/**
 * 支付相关接口
 */
public interface PayService extends PaymentService {

    /**
     * 提交一个支付任务，返回任务相关信息，方便后续查询。
     *
     * @param payParams 支付参数
     * @return 支付任务信息
     */
    PayTask submit(PayParams payParams);

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
