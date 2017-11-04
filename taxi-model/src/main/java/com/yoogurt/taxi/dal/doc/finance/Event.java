package com.yoogurt.taxi.dal.doc.finance;

import com.yoogurt.taxi.dal.bo.Notify;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Setter
@Getter
public class Event<T extends Notify> {

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
		this.created = System.currentTimeMillis();
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
