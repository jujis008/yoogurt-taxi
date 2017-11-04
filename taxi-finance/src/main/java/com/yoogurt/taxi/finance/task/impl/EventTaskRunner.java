package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.service.EventService;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import com.yoogurt.taxi.finance.task.TaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 执行回调任务
 */
@Slf4j
@Service("eventTaskRunner")
public class EventTaskRunner implements TaskRunner<EventTask> {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private EventService eventService;

    @Autowired
    private RedisHelper redis;


    private NotifyService notifyService;

    /**
     * 通过第三方交易平台，执行特定的任务
     *
     * @param eventTask 任务信息
     */
    @Override
    public void run(EventTask eventTask) {
//        if(eventTask == null) return;
//        final Event event = eventTask.getEvent();
//        log.info("[taxi-order#" + eventTask.getTaskId() + "]" + event.getEventType());
//        Notify notify = event.getData();
//        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
//        if(payChannel == null || StringUtils.isBlank(payChannel.getServiceName())) return;
//        NotifyService notifyService = (NotifyService) context.getBean(payChannel.getServiceName());
//        CompletableFuture<ResponseObj> future = payChannelService.doTask(payTask);
//        if (future == null) return;
//        future.thenAccept(obj -> {
//            log.warn(obj.getMessage());
//
//            final String taskId = eventTask.getTaskId();
//            final TaskInfo task = eventTask.getTask();
//            event.setRetryTimes(task.getRetryTimes().get());
//            if (obj.isSuccess()) { //执行成功
//                //状态码
//                task.setStatusCode(TaskStatus.EXECUTE_SUCCESS.getCode());
//                //提示信息
//                task.setMessage(TaskStatus.EXECUTE_SUCCESS.getMessage());
//                //任务结束时间
//                task.setEndTimestamp(System.currentTimeMillis());
//                //支付对象持久化
//                eventService.addEvent(event);
//                //支付任务持久化
//                eventService.addEventTask(eventTask);
//                //缓存Payment对象
//                redis.put(CacheKey.NOTIFY_MAP, CacheKey.EVENT_HASH_KEY + taskId, event);
//                //删除任务信息缓存
//                redis.deleteMap(CacheKey.NOTIFY_MAP, CacheKey.TASK_HASH_KEY + taskId);
//                log.info("[" + task.getTaskId() + "]任务执行完毕!");
//            } else if (task.isNeedRetry()) {//触发任务重试
//                if (task.canRetry()) {
//                    log.warn("[" + task.getTaskId() + "]任务执行失败!!");
//                    notifyService.retry(taskId); //重试
//                } else {
//                    //状态码
//                    task.setStatusCode(TaskStatus.EXECUTE_FAILED.getCode());
//                    //提示信息
//                    task.setMessage(TaskStatus.EXECUTE_FAILED.getMessage());
//                    //将最终的任务状态更新到mongo中，没有此任务信息，会自动创建
//                    eventService.saveEventTask(eventTask);
//                    //删除任务信息缓存
//                    redis.deleteMap(CacheKey.NOTIFY_MAP, CacheKey.TASK_HASH_KEY + taskId);
//                    log.error("[" + task.getTaskId() + "]重试次数已达上限，任务执行失败!!!");
//                }
//            }
//        });
    }
}
