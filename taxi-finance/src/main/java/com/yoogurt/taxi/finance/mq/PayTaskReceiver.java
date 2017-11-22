package com.yoogurt.taxi.finance.mq;

import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.mq.TaskReceiver;
import com.yoogurt.taxi.pay.runner.TaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = MessageQueue.PAY_QUEUE_NAME)
public class PayTaskReceiver implements TaskReceiver<PayTask> {

    @Autowired
    private TaskRunner<PayTask> payTaskRunner;

    @RabbitHandler
    @Override
    public void receive(@Payload PayTask payTask) {
        if (payTask == null) {
            return;
        }
        TaskInfo task = payTask.getTask();
        //只接收处于 【可执行状态】 的支付任务
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        if (status == null || !status.isExecutable()) {
            return;
        }
        log.info("[" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss") + "] 收到支付任务，开始执行......");
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        payTaskRunner.run(payTask);
    }
}
