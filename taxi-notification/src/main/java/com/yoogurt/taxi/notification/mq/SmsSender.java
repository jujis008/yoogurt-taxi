package com.yoogurt.taxi.notification.mq;

import com.alibaba.fastjson.JSON;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.bo.SmsPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(SmsPayload payload) {

        if (payload == null) return;
        log.info("消息入队："+ JSON.toJSONString(payload));
        rabbitTemplate.convertAndSend(MessageQueue.SMS_NOTIFICATION_QUEUE.getExchange(), MessageQueue.SMS_NOTIFICATION_QUEUE.getRoutingKey(), payload);
    }
}
