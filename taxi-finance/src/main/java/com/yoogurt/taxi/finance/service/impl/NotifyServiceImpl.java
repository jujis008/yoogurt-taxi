package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class NotifyServiceImpl implements NotifyService {

    @Autowired
    private TaskSender<EventTask> eventTaskSender;

    @Autowired
    private RedisHelper redis;


    /**
     * 提交一个回调任务
     *
     * @param task 回调任务
     * @return EventTask。如果提交失败，将会返回null
     */
    @Override
    public EventTask submit(Event task) {

        try {
            return doSubmit(task, null, false);
        } catch (Exception e) {
            log.error("提交回调任务失败, {}", e);
        }
        return null;
    }

    /**
     * 获取一个回调任务
     *
     * @param taskId 任务id
     * @return EventTask
     */
    @Override
    public EventTask getTask(String taskId) {

        Object o = redis.getMapValue(CacheKey.NOTIFY_MAP, CacheKey.TASK_HASH_KEY + taskId);
        if (o == null) return null;
        return (EventTask) o;
    }

    /**
     * 获取回调任务执行结果。
     *
     * @param taskId 任务id
     * @return 回调对象
     */
    @Override
    public <T extends Notify> Event<T> queryResult(String taskId) {

        Object o = redis.getMapValue(CacheKey.NOTIFY_MAP, CacheKey.EVENT_HASH_KEY + taskId);
        if (o == null) return null;
        return (Event<T>) o;
    }

    /**
     * 取消回调任务
     *
     * @param taskId 任务id
     * @return EventTask
     */
    @Override
    public EventTask cancel(String taskId) {
        return null;
    }

    /**
     * 任务重试
     *
     * @param taskId 任务id
     * @return EventTask
     */
    @Override
    public EventTask retry(String taskId) {

        try {
            return doSubmit(null, taskId, true);
        } catch (Exception e) {
            log.error("回调任务重试异常,{}", e);
        }
        return null;
    }


    private TaskInfo buildTask() {
        String taskId = "pt_" + RandomUtils.getPrimaryKey();
        TaskInfo taskInfo = new TaskInfo(taskId);
        taskInfo.setQueueName("X-Queue-Pay-Notify");
        taskInfo.setRoutingKey("topic.task.notify.pay");
        return taskInfo;
    }

    private EventTask doSubmit(Event event, String taskId, boolean isRetry) {
        final EventTask task;
        if (isRetry && StringUtils.isNoneBlank(taskId)) {
            task = getTask(taskId);
            if(task != null) task.getTask().doRetry();
        } else {
            TaskInfo taskInfo = buildTask();
            taskId = taskInfo.getTaskId();
            task = new EventTask(taskId, taskInfo, event);
        }
        //重新设置任务信息缓存
        redis.put(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId, task);
        eventTaskSender.send(task);
        return task;
    }
}
