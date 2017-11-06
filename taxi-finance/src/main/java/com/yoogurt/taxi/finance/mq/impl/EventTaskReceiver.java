package com.yoogurt.taxi.finance.mq.impl;

import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.mq.TaskReceiver;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.TaskInfo;
import com.yoogurt.taxi.finance.task.TaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class EventTaskReceiver<Runner extends TaskRunner<EventTask>> implements TaskReceiver<EventTask> {

    @Autowired
    private Runner eventTaskRunner;

    @RabbitHandler
    @Override
    public void receive(EventTask eventTask) {

        if (eventTask == null) return;
        TaskInfo task = eventTask.getTask();
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        //只接收处于 【可执行状态】 的支付任务
        if (status == null || !status.isExecutable()) return;
        log.info("[" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "] 收到支付任务，开始执行......");
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        eventTaskRunner.run(eventTask);
    }
}
