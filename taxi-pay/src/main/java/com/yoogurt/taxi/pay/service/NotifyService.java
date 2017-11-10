package com.yoogurt.taxi.pay.service;

import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;

import java.util.Map;

/**
 * 回调相关接口
 */
public interface NotifyService {

    /**
     * 解析回调请求，组装EventTask
     *
     * @param parameterMap 回调请求
     * @return EventTask
     */
    Event<? extends Notify> eventParse(Map<String, Object> parameterMap);

    /**
     * 提交一个回调任务
     *
     * @param event 回调事件
     * @return EventTask。如果提交失败，将会返回null
     */
    EventTask submit(Event<? extends Notify> event);

    /**
     * 获取一个回调任务
     *
     * @param taskId 任务id
     * @return EventTask
     */
    EventTask getTask(String taskId);

    /**
     * 获取回调任务执行结果。
     *
     * @param taskId 任务id
     * @return 回调对象
     */
    <T extends Notify> Event<T> queryResult(String taskId);

    /**
     * 取消回调任务
     *
     * @param taskId 任务id
     * @return EventTask
     */
    EventTask cancel(String taskId);

    /**
     * 任务重试
     *
     * @param taskId 任务id
     * @return EventTask
     */
    EventTask retry(String taskId);
}
