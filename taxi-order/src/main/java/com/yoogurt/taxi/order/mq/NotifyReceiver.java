package com.yoogurt.taxi.order.mq;

import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.task.EventTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = "X-Queue-Pay-Notify")
public class NotifyReceiver {

    @Autowired
    private ApplicationContext context;

    @RabbitHandler
    public void receive(@Payload EventTask eventTask) {

        if (eventTask == null) return;
        Event event = eventTask.getEvent();
        log.info("[taxi-order#" + eventTask.getTaskId() + "]" + event.getEventType());
        Notify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if(payChannel == null || StringUtils.isBlank(payChannel.getServiceName())) return;
        NotifyService notifyService = (NotifyService) context.getBean(payChannel.getServiceName());

    }
}
