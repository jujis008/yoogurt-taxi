package com.yoogurt.taxi.finance.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@NoArgsConstructor
public class TaskInfo implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * [RabbitMQ]消息交换机
     */
    @JsonIgnore
    private String exchangeName = "X-Exchange-Pay";

    /**
     * [RabbitMQ]消息路由的key
     */
    @JsonIgnore
    private String routingKey = "topic.task.pay";

    /**
     * [RabbitMQ]任务队列名称，由数字或字母组成，不允许特殊字符。
     */
    @JsonIgnore
    private String queueName = "X-Queue-Pay";

    /**
     * 是否需要重试，默认true
     */
    private boolean needRetry = true;

    /**
     * 当前任务重试的次数（不含第一次执行），最大重试次数内置为5次。
     */
    private AtomicInteger retryTimes = new AtomicInteger(0);

    /**
     * 上次任务重试的时间戳
     */
    private long lastRetryTimestamp = 0;

    /**
     * 任务执行的开始时间戳
     */
    private long startTimestamp;

    /**
     * 任务结束的时间戳
     */
    private long endTimestamp;

    /**
     * 任务执行状态码
     */
    private String statusCode = TaskStatus.EXECUTE_READY.getCode();

    /**
     * 提示信息
     */
    private String message = TaskStatus.EXECUTE_READY.getMessage();


    public TaskInfo(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 重试次数加1
     */
    private int incrRetryTimes() {
        return this.retryTimes.incrementAndGet();
    }

    /**
     * 设置重试时间
     */
    private void retryTimestamp() {
        this.lastRetryTimestamp = System.currentTimeMillis();
    }

    /**
     * 重试状态修改
     */
    private void retryStatus() {
        this.statusCode = TaskStatus.EXECUTE_LATER.getCode();
        this.message = TaskStatus.EXECUTE_LATER.getMessage();
    }

    /**
     * 记录重试动作
     */
    public void retryRecord() {
        incrRetryTimes();
        retryTimestamp();
        retryStatus();
    }

    /**
     * 重试时间间隔以公差为1的等差数列组成。
     * 系统内置有一个最大重试次数，最大重试次数内置为5次。
     *
     * @return 重试的间隔时间，单位：毫秒
     */
    public long retryInterval() throws IllegalAccessException {
        if (!canRetry()) throw new IllegalAccessException("The PayTask cannot retry any more.");
        return (this.retryTimes.get() + 1) * 1000;
    }

    public boolean canRetry() {
        TaskStatus status = TaskStatus.getEnumByStatus(this.statusCode);
        return status != null && status.isExecutable() && this.retryTimes.get() < Constants.MAX_TASK_RETRY_TIMES;
    }
}
