package com.yoogurt.taxi.order.mq;

import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.order.mq.task.EventTaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = MessageQueue.ORDER_NOTIFY_QUEUE_NAME)
public class OrderPayNotifyReceiver {

    @Autowired
    private EventTaskRunner eventTaskRunner;

    @RabbitHandler
    public void receive(@Payload EventTask eventTask) {

        if (eventTask == null) return;
        TaskInfo task = eventTask.getTask();
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        //只接收处于 【可执行状态】 的支付任务
        if (status == null || !status.isExecutable()) return;
        log.info("[" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "] 收到回调任务，开始执行......");
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        eventTaskRunner.run(eventTask);
    }

}
