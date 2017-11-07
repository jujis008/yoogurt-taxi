package com.yoogurt.taxi.dal.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TaskInfo implements Serializable {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * [RabbitMQ]消息交换机，默认使用 X-Exchange-Pay
     */
    @JsonIgnore
    private MessageQueue messageQueue;

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

    /**
     * 该任务是否可以重试
     * @return true-可以重试 false-不可再重试
     */
    public boolean canRetry() {
        TaskStatus status = TaskStatus.getEnumByStatus(this.statusCode);
        return status != null && status.isExecutable() && this.retryTimes.get() < Constants.MAX_TASK_RETRY_TIMES;
    }

    /**
     * 任务重试动作，主要将线程sleep一段时间。
     */
    public void doRetry() {
        try {
            long interval = retryInterval();
            log.warn("[" + getTaskId() + "]" + interval + "ms后重试……");
            //重试间隔，让线程睡一会儿
            Thread.sleep(interval);
            //记录重试操作
            retryRecord();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } catch (IllegalAccessException e) {
            log.warn("[" + this.taskId + "]重试失败, {}", e);
        }
    }
}
