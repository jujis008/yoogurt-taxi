package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.task.PayTask;

import java.util.concurrent.TimeUnit;

public interface IPayService {

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
     * 获取支付任务执行结果，可设置获取超时时间。
     *
     * @param taskId  任务id
     * @param timeout 超时时长
     * @param unit    时长单位
     * @return 支付对象
     */
    Payment queryResult(String taskId, long timeout, TimeUnit unit);

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

}
