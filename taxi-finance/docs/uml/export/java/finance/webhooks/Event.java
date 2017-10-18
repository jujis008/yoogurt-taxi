package finance.webhooks;

public class Event {

	private String eventId;

	/**
	 * 事件发生的时间，精确到秒
	 */
	private int created;

	/**
	 * 重试次数
	 */
	private int retryTimes;

	/**
	 * pay.succeeded 支付对象，支付成功时触发。
	 * refund.succeeded 退款对象，退款成功时触发。
	 * transfer.succeeded 企业支付对象，支付成功时触发。
	 * batch_transfer.succeeded 批量企业付款对象，批量企业付款成功时触发。
	 * batch_refund.succeeded 批量退款对象，批量退款成功时触发。
	 */
	private String eventType;

	/**
	 * 如果是支付成功事件，则返回对应的Payment对象；
	 * 如果是退款成功时间，则返回对应的Refund对象。
	 */
	private Object data;

}
