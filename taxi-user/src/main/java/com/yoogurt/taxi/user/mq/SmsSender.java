package com.yoogurt.taxi.user.mq;

import com.yoogurt.taxi.dal.bo.SmsPayload;
import com.yoogurt.taxi.dal.enums.MessageQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SmsPayload payload) {

        if (payload == null) return;
        rabbitTemplate.convertAndSend(MessageQueue.SMS_NOTIFICATION_QUEUE.getExchange(), MessageQueue.SMS_NOTIFICATION_QUEUE.getRoutingKey(), payload);
    }
}
