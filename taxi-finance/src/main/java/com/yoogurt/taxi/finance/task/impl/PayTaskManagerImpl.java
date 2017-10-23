package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.mq.PayTaskSender;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskManager;
import com.yoogurt.taxi.finance.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PayTaskManagerImpl implements PayTaskManager {

    @Autowired
    private PayTaskSender sender;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public PayTask submit(PayParams params) {
        PayTask task = new PayTask(buildTask(), params);
        sender.send(task);
        return task;
    }

    /**
     * 取消支付任务，任务状态变为EXECUTE_CANCELED
     *
     * @param taskId
     */
    @Override
    public PayTask cancel(String taskId) {
        return null;
    }

    /**
     * 获取支付任务信息
     *
     * @param taskId
     */
    @Override
    public PayTask getTask(String taskId) {
        return null;
    }

    /**
     * 获取支付任务执行结果。
     *
     * @param taskId
     */
    @Override
    public Payment queryResult(String taskId) {
        return null;
    }

    /**
     * 获取支付任务执行结果，可设置获取超时时间。
     *
     * @param taskId
     * @param timeout
     * @param unit
     */
    @Override
    public Payment queryResult(String taskId, long timeout, TimeUnit unit) {
        return null;
    }

    /**
     * 任务重试
     *
     * @param params
     * @return
     */
    @Override
    public PayTask retry(PayParams params) {
        return null;
    }

    private TaskInfo buildTask() {
        String taskId = "pay_" + RandomUtils.getPrimaryKey();
        return new TaskInfo(taskId);
    }
}
