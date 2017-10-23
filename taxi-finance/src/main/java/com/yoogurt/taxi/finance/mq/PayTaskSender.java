package com.yoogurt.taxi.finance.mq;

import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = "X-Queue-Pay")
public class PayTaskSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void send(PayTask payTask) {

        TaskInfo task = payTask.getTask();
        rabbitTemplate.convertAndSend(task.getExchangeName(), task.getRoutingKey(), payTask);
    }
}
