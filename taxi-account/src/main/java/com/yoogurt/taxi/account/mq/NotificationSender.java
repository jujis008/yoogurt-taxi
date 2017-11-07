package com.yoogurt.taxi.account.mq;

import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.common.enums.MessageQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 推送请求进入消息队列。
     * @param payload 消息负载
     */
    public void send(PushPayload payload) {
        rabbitTemplate.convertAndSend(MessageQueue.SMS_NOTIFICATION_QUEUE.getExchange(), MessageQueue.SMS_NOTIFICATION_QUEUE.getRoutingKey(), payload);
    }
}