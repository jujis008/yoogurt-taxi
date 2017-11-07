package com.yoogurt.taxi.finance.mq.impl;

import com.yoogurt.taxi.dal.enums.MessageQueue;
import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.finance.task.PayTask;
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
