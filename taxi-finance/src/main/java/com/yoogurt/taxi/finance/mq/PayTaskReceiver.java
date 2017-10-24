package com.yoogurt.taxi.finance.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = "X-Queue-Pay")
public class PayTaskReceiver {

    @RabbitHandler
    public void receive() {

    }
}
