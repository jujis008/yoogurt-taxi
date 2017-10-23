package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

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
	private String exchangeName = "X-Exchange-Pay";

	/**
	 * [RabbitMQ]消息路由的key
	 */
	private String routingKey = "topic.task.pay";

	/**
	 * [RabbitMQ]任务队列名称，由数字或字母组成，不允许特殊字符。
	 */
	private String queueName = "X-Queue-Pay";

	/**
	 * 任务执行的开始时间戳
	 */
	private long startTimestamp;

	/**
	 * 任务结束的时间戳
	 */
	private long endTimestamp;

	/**
	 * 是否需要重试，默认true
	 */
	private boolean needRetry = true;

	/**
	 * 任务重试次数，默认三次，分别间隔1s，2s，3s
	 * 间隔时间以公差为1的等差数列组成，最大重试次数内置为5次。
	 */
	private int retryTimes = 3;

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
}
