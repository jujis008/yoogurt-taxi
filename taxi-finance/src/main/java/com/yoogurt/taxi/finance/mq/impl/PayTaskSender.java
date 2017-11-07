package com.yoogurt.taxi.finance.mq.impl;

import com.yoogurt.taxi.finance.mq.TaskSender;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.dal.bo.TaskInfo;
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

            TaskInfo task = payTask.getTask();
            log.info(task.toString());
            rabbitTemplate.convertAndSend(task.getExchangeName(), task.getRoutingKey(), payTask);
        }
    }
}
