package com.yoogurt.taxi.notification.mq;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RabbitListener(queues = "X-Queue-Notification")
public class PushReceiver {

    @RabbitHandler
    public void receive(@Payload String msg) {

        log.info(DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "收到消息：" + msg);
    }
}
