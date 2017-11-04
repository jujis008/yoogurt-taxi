package com.yoogurt.taxi.finance.mq.impl;

import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("payTaskSender")
public class PayTaskSender implements TaskSender<PayTask> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEventTask(EventTask eventTask) {

        if (eventTask != null) {

            TaskInfo task = eventTask.getTask();
            rabbitTemplate.convertAndSend(task.getExchangeName(), task.getRoutingKey(), eventTask);
        }
    }

    @Override
    public void send(PayTask payTask) {
        if (payTask != null) {

            TaskInfo task = payTask.getTask();
            rabbitTemplate.convertAndSend(task.getExchangeName(), task.getRoutingKey(), payTask);
        }
    }
}
