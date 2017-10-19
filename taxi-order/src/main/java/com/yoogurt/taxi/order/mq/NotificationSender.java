package com.yoogurt.taxi.order.mq;

import com.yoogurt.taxi.dal.bo.PushPayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(PushPayload payload) {
        rabbitTemplate.convertAndSend("X-Exchange-Notification", "topic.notification.push.order", payload);
    }
}