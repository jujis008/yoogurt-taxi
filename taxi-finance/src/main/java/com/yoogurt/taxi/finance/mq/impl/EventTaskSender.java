package com.yoogurt.taxi.finance.mq.impl;

import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.dal.enums.MessageQueue;
import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("eventTaskSender")
public class EventTaskSender implements TaskSender<EventTask> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(EventTask eventTask) {
        if (eventTask != null) {

            MessageQueue messageQueue = eventTask.getTask().getMessageQueue();
            rabbitTemplate.convertAndSend(messageQueue.getExchange(), messageQueue.getRoutingKey(), eventTask);
        }
    }
}
