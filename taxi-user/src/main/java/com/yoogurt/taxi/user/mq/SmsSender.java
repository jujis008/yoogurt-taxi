package com.yoogurt.taxi.user.mq;

import com.yoogurt.taxi.dal.bo.SmsPayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsSender {
    private final static String EXCHANGE = "X-Exchange-Notification";

    private final static String ROUTING_KEY = "topic.sms.phoneCode";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SmsPayload payload) {
        if (payload == null) return;
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, payload);
    }
}
