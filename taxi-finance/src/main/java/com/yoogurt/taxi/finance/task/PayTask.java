package com.yoogurt.taxi.finance.task;

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


}
