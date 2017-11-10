package com.yoogurt.taxi.pay.mq.impl;

import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.mq.TaskSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("payTaskSender")
public class PayTaskSender implements TaskSender<PayTask> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(PayTask payTask) {
        if (payTask != null) {

            MessageQueue messageQueue = payTask.getTask().getMessageQueue();
            log.info(messageQueue.toString());
            rabbitTemplate.convertAndSend(messageQueue.getExchange(), messageQueue.getRoutingKey(), payTask);
        }
    }
}
