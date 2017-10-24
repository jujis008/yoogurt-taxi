package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.doc.finance.PayParams;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.mq.PayTaskSender;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskManager;
import com.yoogurt.taxi.finance.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PayTaskManagerImpl implements PayTaskManager {

    @Autowired
    private PayTaskSender sender;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 根据支付参数提交支付任务。
     * 支付任务将会缓存在Redis中。
     *
     * @param params 支付参数
     * @return 生成的支付任务信息
     */
    @Override
    public PayTask submit(PayParams params) {
        return doSubmit(params, null,false);
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

        return (PayTask) redisHelper.getMapValue(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY);
    }

    /**
     * 查询支付任务执行结果。
     *
     * @param taskId 任务ID
     */
    @Override
    public Payment queryResult(String taskId) {

        return (Payment) redisHelper.getMapValue(CacheKey.PAY_MAP, CacheKey.PAYMENT_HASH_KEY);
    }


    /**
     * 任务重试，这里重新提交给MQ进行消费。
     *
     * @param taskId 支付任务ID
     * @return 任务信息
     */
    @Override
    public PayTask retry(String taskId) {
        return doSubmit(null, taskId, true);
    }

    private TaskInfo buildTask() {
        String taskId = "pay_" + RandomUtils.getPrimaryKey();
        return new TaskInfo(taskId);
    }

    private PayTask doSubmit(PayParams params, String taskId, boolean isRetry) {
        final PayTask task;
        if (isRetry && StringUtils.isBlank(taskId)) {
            task = getTask(taskId);
        } else {
            task = new PayTask(buildTask(), params);
            redisHelper.setMap(CacheKey.PAY_MAP, new HashMap<String, Object>() {{
                put(CacheKey.TASK_HASH_KEY, task);
            }});
        }
        sender.send(task);
        return task;
    }
}
