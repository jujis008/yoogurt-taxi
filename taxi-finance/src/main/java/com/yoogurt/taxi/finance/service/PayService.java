package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;

import java.util.concurrent.TimeUnit;

/**
 * 生成支付任务，提交给MQ
 */
public interface PayTaskManager {

    /**
     * 提交一个支付任务，返回任务相关信息，方便后续查询。
     *
     * @param params 支付参数
     * @return 支付任务信息
     */
    PayTask submitPayTask(PayParams params);

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
    PayTask cancelPayTask(String taskId);

    /**
     * 根据id获取支付对象。
     *
     * @param payId 支付对象id
     * @return 支付对象信息
     */
    Payment getPayment(String payId);


    /**
     * 任务重试
     *
     * @param taskId 任务id
     * @return 任务信息
     */
    PayTask retry(String taskId);

}
