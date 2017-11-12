package com.yoogurt.taxi.account.mq;

import com.yoogurt.taxi.account.mq.task.ChargeNotifyTaskRunner;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.pay.doc.EventTask;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = MessageQueue.CHARGE_NOTIFY_QUEUE_NAME)
public class ChargeNotifyReceiver {

    @Autowired
    private ChargeNotifyTaskRunner chargeNotifyTaskRunner;

    @RabbitHandler
    public void receive(@Payload EventTask eventTask) {
        if (eventTask == null) return;
        TaskInfo task = eventTask.getTask();
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        //只接收处于 【可执行状态】 的支付任务
        if (status == null || !status.isExecutable()) return;
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        log.info("[" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "] 收到回调任务，开始执行......");
        chargeNotifyTaskRunner.notify(eventTask);
    }
}
