package com.yoogurt.taxi.notification.mq;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.dal.enums.MessageQueue;
import com.yoogurt.taxi.notification.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RabbitListener(queues = MessageQueue.NOTIFICATION_QUEUE_NAME)
public class PushReceiver {

    @Autowired
    private PushService pushService;

    @RabbitHandler
    public void receive(@Payload PushPayload payload) {

        log.info(DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "收到消息：" + payload.toString());

        ResponseObj obj = pushService.pushMessage(payload.getUserIds(), payload.getUserType(), payload.getSendType(), payload.getMsgType(), payload.getDeviceType(), payload.getTitle(), payload.getContent(), payload.getExtras(), payload.isPersist());

        log.info("推送结果：" + obj.toJSON());
    }
}
