package com.yoogurt.taxi.pay.runner.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.pay.doc.Event;
import com.yoogurt.taxi.pay.doc.EventTask;
import com.yoogurt.taxi.pay.runner.TaskRunner;
import com.yoogurt.taxi.pay.service.EventService;
import com.yoogurt.taxi.pay.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractEventTaskRunner implements TaskRunner<EventTask> {

    @Autowired
    private EventService eventService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RedisHelper redis;

    public abstract CompletableFuture<ResponseObj> doTask(EventTask eventTask);

    @Override
    public void run(final EventTask eventTask) {
        if (eventTask == null) {
            return;
        }
        final TaskInfo task = eventTask.getTask();
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        //只接收处于 【可执行状态】 的支付任务
        if (status == null || !status.isExecutable()) {
            return;
        }
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        CompletableFuture<ResponseObj> future = doTask(eventTask);
        if (future == null) {
            return;
        }
        future.thenAccept(obj -> {
            final String taskId = task.getTaskId();
            //执行成功
            if (obj.isSuccess()) {
                //状态码
                task.setStatusCode(TaskStatus.EXECUTE_SUCCESS.getCode());
                //提示信息
                task.setMessage(TaskStatus.EXECUTE_SUCCESS.getMessage());
                //任务结束时间
                task.setEndTimestamp(System.currentTimeMillis());
                Object o = obj.getBody();
                if (o != null && o instanceof Event) {
                    //回调任务持久化
                    eventService.saveEventTask(eventTask);
                    //回调对象持久化
                    eventService.addEvent((Event) o);
                    //缓存Payment对象
                    redis.put(CacheKey.NOTIFY_MAP, CacheKey.EVENT_HASH_KEY + taskId, o);
                }
                //删除任务信息缓存
                redis.deleteMap(CacheKey.NOTIFY_MAP, CacheKey.TASK_HASH_KEY + taskId);
                log.info("[" + task.getTaskId() + "]任务执行完毕!");
            } else {
                retry(eventTask);
            }
        });
    }

    @Override
    public EventTask retry(EventTask eventTask) {
        TaskInfo task = eventTask.getTask();
        String taskId = task.getTaskId();
        //触发任务重试
        if (task.isNeedRetry()) {
            if (task.canRetry()) {
                log.warn("[" + task.getTaskId() + "]任务执行失败!!");
                //重试
                return notifyService.retry(taskId);
            } else {
                //状态码
                task.setStatusCode(TaskStatus.EXECUTE_FAILED.getCode());
                //提示信息
                task.setMessage(TaskStatus.EXECUTE_FAILED.getMessage());
                //将最终的任务状态更新到mongo中，没有此任务信息，会自动创建
                eventService.saveEventTask(eventTask);
                //删除任务信息缓存
                redis.deleteMap(CacheKey.NOTIFY_MAP, CacheKey.TASK_HASH_KEY + taskId);
                log.error("[" + task.getTaskId() + "]重试次数已达上限，任务执行失败!!!");
            }
        }
        return eventTask;
    }
}
