package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * 支付任务执行状态
 */
@Getter
public enum TaskStatus {

	/**
	 * 任务创建成功，准备执行
	 */
	EXECUTE_READY("ready", "任务创建成功"),

	/**
	 * 执行成功,这种情况 直接反馈客户端
	 */
	EXECUTE_SUCCESS("success", "任务执行成功"),

	/**
	 * 执行失败,这种情况,直接反馈给客户端,不重新执行
	 */
	EXECUTE_FAILED("failed", "任务执行失败"),

	/**
	 * 稍后重新执行,这种情况, 不反馈客户端,稍后重新执行,不过有最大重试次数
	 */
	EXECUTE_LATER("later", "任务正在进入重试"),

	/**
	 * 执行异常, 这中情况也会重试
	 */
	EXECUTE_EXCEPTION("exception", "任务执行异常"),

	/**
	 * 任务被取消
	 */
	EXECUTE_CANCELED("canceled", "任务已取消"),
	;

	String code;

	String message;

	TaskStatus(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public static TaskStatus getEnumByStatus(String status) {

		if (!StringUtils.isBlank(status)) {
			for (TaskStatus s : TaskStatus.values()) {
				if (status.equals(s.getCode())) {
					return s;
				}
			}
		}
		return null;
	}

	public boolean isExecutable() {
		switch (this) {
			case EXECUTE_READY:
			case EXECUTE_LATER:
				return true;
			default:
				return false;
		}
	}
}
