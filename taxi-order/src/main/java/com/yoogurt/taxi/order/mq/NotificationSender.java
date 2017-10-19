package com.yoogurt.taxi.order.mq;

import com.yoogurt.taxi.dal.bo.PushPayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    private final static String EXCHANGE = "X-Exchange-Notification";

    private final static String ROUTING_KEY = "topic.notification.push.order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 推送请求进入消息队列。
     * @param payload 消息负载
     */
    public void send(PushPayload payload) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
    }
}