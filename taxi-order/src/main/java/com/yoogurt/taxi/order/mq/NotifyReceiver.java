package com.yoogurt.taxi.order.mq;

import com.yoogurt.taxi.finance.mq.impl.EventTaskReceiver;
import com.yoogurt.taxi.order.mq.task.EventTaskRunner;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@RabbitListener(queues = "X-Queue-Pay-Notify")
public class NotifyReceiver extends EventTaskReceiver<EventTaskRunner> {

}
