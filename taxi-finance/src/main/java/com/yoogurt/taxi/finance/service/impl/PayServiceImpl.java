package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayServiceImpl extends PaymentServiceImpl implements PayService {

    @Autowired
    private TaskSender<PayTask> payTaskSender;

    @Autowired
    private RedisHelper redis;

    /**
     * 根据支付参数提交支付任务。
     * 支付任务将会缓存在Redis中。
     *
     * @param form 支付参数
     * @return 生成的支付任务信息
     */
    @Override
    public PayTask submit(PayForm form) {
        try {
            return doSubmit(form, null, false);
        } catch (Exception e) {
            log.error("提交支付任务失败,{}", e);
        }
        return null;
    }

    /**
     * 取消支付任务，任务状态变为EXECUTE_CANCELED
     *
     * @param taskId 任务ID
     * @return 生成的支付任务信息
     */
    @Override
    public PayTask cancel(String taskId) {
        return null;
    }

    /**
     * 获取支付任务信息
     *
     * @param taskId 任务ID
     */
    @Override
    public PayTask getTask(String taskId) {

        Object o = redis.getMapValue(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId);
        if (o == null) return null;
        return (PayTask) o;
    }

    /**
     * 查询支付任务执行结果。
     *
     * @param taskId 任务ID
     */
    @Override
    public Payment queryResult(String taskId) {

        Object o = redis.getMapValue(CacheKey.PAY_MAP, CacheKey.PAYMENT_HASH_KEY + taskId);
        if (o == null) return null;
        return (Payment) o;
    }


    /**
     * 任务重试，这里重新提交给MQ进行消费。
     *
     * @param taskId 支付任务ID
     * @return 任务信息
     */
    @Override
    public PayTask retry(String taskId) {

        try {
            return doSubmit(null, taskId, true);
        } catch (Exception e) {
            log.error("支付任务重试异常,{}", e);
        }
        return null;
    }

    private TaskInfo buildTask() {
        String taskId = "pt_" + RandomUtils.getPrimaryKey();
        TaskInfo taskInfo = new TaskInfo(taskId);
        taskInfo.setQueueName("X-Queue-Pay");
        taskInfo.setRoutingKey("topic.task.pay");
        return taskInfo;
    }

    private PayTask doSubmit(PayForm form, String taskId, boolean isRetry) {
        final PayTask task;
        if (isRetry && StringUtils.isNoneBlank(taskId)) {
            task = getTask(taskId);
            if(task != null) task.getTask().doRetry();
        } else {
            TaskInfo taskInfo = buildTask();
            taskId = taskInfo.getTaskId();
            task = new PayTask(taskId, taskInfo, form);
        }
        //重新设置任务信息缓存
        redis.put(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId, task);
        payTaskSender.send(task);
        return task;
    }
}
