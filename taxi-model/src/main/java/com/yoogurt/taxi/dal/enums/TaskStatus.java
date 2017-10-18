package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 支付任务执行状态
 */
@Getter
public enum TaskStatus {

	/**
	 * 执行成功,这种情况 直接反馈客户端
	 */
	EXECUTE_SUCCESS,

	/**
	 * 执行失败,这种情况,直接反馈给客户端,不重新执行
	 */
	EXECUTE_FAILED,

	/**
	 * 稍后重新执行,这种情况, 不反馈客户端,稍后重新执行,不过有最大重试次数
	 */
	EXECUTE_LATER,

	/**
	 * 执行异常, 这中情况也会重试
	 */
	EXECUTE_EXCEPTION,

	/**
	 * 任务被取消
	 */
	EXECUTE_CANCELED,
	;

}
