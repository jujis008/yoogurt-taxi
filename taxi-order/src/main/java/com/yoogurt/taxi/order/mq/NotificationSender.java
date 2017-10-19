package com.yoogurt.taxi.order.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(1000);
                String msg = "我要发推送消息";
                rabbitTemplate.convertAndSend("X-Exchange-Notification", "topic.notification.push.order", msg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
