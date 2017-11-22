package com.yoogurt.taxi.pay.doc;

import com.yoogurt.taxi.dal.bo.BaseNotify;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class Event<T extends BaseNotify> implements Serializable {

	@Id
	private String eventId;

	/**
	 * 事件发生的时间戳
	 */
	private long created;

	/**
	 * pay.succeeded 支付对象，支付成功时触发。
	 * refund.succeeded 退款对象，退款成功时触发。
	 * transfer.succeeded 企业支付对象，支付成功时触发。
	 * batch_transfer.succeeded 批量企业付款对象，批量企业付款成功时触发。
	 * batch_refund.succeeded 批量退款对象，批量退款成功时触发。
	 */
	private String eventType;

	/**
	 * 回调重试的次数
	 */
	private int retryTimes = 0;

	/**
	 * 回调对象。
	 */
	private T data;

	public Event() {

	}

	public Event(String eventId) {
		this();
		this.eventId = eventId;
	}

	public Event(String eventId, T data) {
		this();
		this.eventId = eventId;
		this.data = data;
	}
}
