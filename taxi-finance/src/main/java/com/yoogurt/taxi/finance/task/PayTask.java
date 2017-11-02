package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.form.PayForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayTask implements Serializable {

    @Id
    private String taskId;

    private TaskInfo task;

    private PayForm payParams;

    /**
     * 重试次数加1
     */
    private void incrRetryTimes() {
        this.task.setRetryTimes(this.task.getRetryTimes() + 1);
    }

    /**
     * 设置重试时间
     */
    private void retryTimestamp() {
        this.task.setLastRetryTimestamp(System.currentTimeMillis());
    }

    /**
     * 重试状态修改
     */
    private void retryStatus() {
        this.task.setStatusCode(TaskStatus.EXECUTE_LATER.getCode());
        this.task.setMessage(TaskStatus.EXECUTE_LATER.getMessage());
    }

    /**
     * 记录重试动作
     */
    public PayTask retryRecord() {
        incrRetryTimes();
        retryTimestamp();
        retryStatus();
        return this;
    }

    /**
     * 重试时间间隔以公差为1的等差数列组成。
     * 系统内置有一个最大重试次数，最大重试次数内置为5次。
     *
     * @return 重试的间隔时间，单位：毫秒
     */
    public long retryInterval() throws IllegalAccessException {
        if(!canRetry()) throw new IllegalAccessException("The PayTask cannot retry any more.");
        return (this.task.getRetryTimes() + 1) * 1000;
    }

    public boolean canRetry() {
        TaskStatus status = TaskStatus.getEnumByStatus(this.task.getStatusCode());
        return status != null && status.isExecutable() && this.task.getRetryTimes() < Constants.MAX_PAY_TASK_RETRY_TIMES;
    }
}
