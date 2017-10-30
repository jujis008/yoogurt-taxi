package com.yoogurt.taxi.finance.mq;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskRunner;
import com.yoogurt.taxi.finance.task.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = "X-Queue-Pay")
public class PayTaskReceiver {

    @Autowired
    private PayService payService;

    @Autowired
    private PayTaskRunner runner;

    @Autowired
    private RedisHelper redis;

    @RabbitHandler
    public void receive(@Payload PayTask payTask) {
        if (payTask == null) return;
        TaskInfo task = payTask.getTask();
        //只接收处于 【可执行状态】 的支付任务
        TaskStatus status = TaskStatus.getEnumByStatus(task.getStatusCode());
        if (status == null || !status.isExecutable()) return;
        String taskId = task.getTaskId();
        log.info("收到支付任务，开始执行......");
        //记录任务开始的时间戳
        task.setStartTimestamp(System.currentTimeMillis());
        Payment payment = runner.run(payTask);
        //执行成功
        if (payment != null) {
            task.setStatusCode(TaskStatus.EXECUTE_SUCCESS.getCode());
            task.setMessage(TaskStatus.EXECUTE_SUCCESS.getMessage());
            //支付对象持久化
            payService.addPayment(payment);
            //支付任务持久化
            payService.addPayTask(payTask);
            //缓存Payment对象
            redis.put(CacheKey.PAY_MAP, CacheKey.PAYMENT_HASH_KEY + taskId, payment);
            //删除任务信息缓存
            redis.deleteMap(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId);
        } else if (task.isNeedRetry()) {
            task.setStatusCode(TaskStatus.EXECUTE_LATER.getCode());
            task.setMessage(TaskStatus.EXECUTE_LATER.getMessage());
            //重新设置任务信息缓存
            redis.put(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId, payTask);
            //重试
            payService.retry(taskId);
        }
    }
}
