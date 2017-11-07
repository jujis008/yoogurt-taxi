package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum MessageQueue {

    /**
     * 支付专用
     */
    PAY_QUEUE(getPayExchange(), "X-Queue-Pay", "topic.task.pay", "order_pay"),
    /**
     * 充值回调专用
     */
    CHARGE_NOTIFY_QUEUE(getNotifyExchange(), "X-Queue-Charge-Notify", "topic.notify.pay", "charge_notify"),
    /**
     * 订单回调专用
     */
    ORDER_NOTIFY_QUEUE(getNotifyExchange(), "X-Queue-Pay-Notify", "topic.notify.charge", "order_notify"),;

    public static final String PAY_QUEUE_NAME = "X-Queue-Pay";

    public static final String CHARGE_NOTIFY_QUEUE_NAME = "X-Queue-Charge-Notify";

    public static final String ORDER_NOTIFY_QUEUE_NAME = "X-Queue-Pay-Notify";

    private String exchange;

    private String queue;

    private String routingKey;

    private String biz;

    MessageQueue(String exchange, String queue, String routingKey, String biz) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
        this.biz = biz;
    }

    public static MessageQueue getEnumsByBiz(String biz) {
        if (StringUtils.isNoneBlank(biz)) {
            for (MessageQueue queue : MessageQueue.values()) {
                if (biz.equals(queue.getBiz())) {
                    return queue;
                }
            }
        }
        return null;
    }

    public static String getPayExchange() {
        return "X-Exchange-Pay";
    }

    public static String getNotifyExchange() {
        return "X-Queue-Pay-Notify";
    }
}
