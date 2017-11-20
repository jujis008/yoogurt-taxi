package com.yoogurt.taxi.account.mq;

import com.alibaba.fastjson.JSON;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.bo.PushPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 推送请求进入消息队列。
     * @param payload 消息负载
     */
    public void send(PushPayload payload) {
        rabbitTemplate.convertAndSend(MessageQueue.ACCOUNT_NOTIFICATION_QUEUE.getExchange(), MessageQueue.ACCOUNT_NOTIFICATION_QUEUE.getRoutingKey(), payload);
        log.info("新的信息推入队列"+ JSON.toJSONString(payload));
    }
}