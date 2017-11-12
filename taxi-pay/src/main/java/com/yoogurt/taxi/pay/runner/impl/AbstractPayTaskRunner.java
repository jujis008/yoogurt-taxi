package com.yoogurt.taxi.pay.runner.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.doc.Payment;
import com.yoogurt.taxi.pay.runner.TaskRunner;
import com.yoogurt.taxi.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractPayTaskRunner implements TaskRunner<PayTask> {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PayService payService;

    @Autowired
    private RedisHelper redis;

    public abstract CompletableFuture<ResponseObj> doTask(PayTask eventTask);

    @Override
    public void run(PayTask payTask) {
        CompletableFuture<ResponseObj> future = doTask(payTask);
        if (future == null) return;
        future.thenAccept(obj -> {
            log.warn(obj.getMessage());

            final String taskId = payTask.getTaskId();
            final TaskInfo task = payTask.getTask();
            if (obj.isSuccess()) { //执行成功
                //状态码
                task.setStatusCode(TaskStatus.EXECUTE_SUCCESS.getCode());
                //提示信息
                task.setMessage(TaskStatus.EXECUTE_SUCCESS.getMessage());
                //任务结束时间
                task.setEndTimestamp(System.currentTimeMillis());
                Payment payment = (Payment) obj.getBody();
                //支付对象持久化
                payService.addPayment(payment);
                //支付任务持久化
                payService.addPayTask(payTask);
                //缓存Payment对象
                redis.put(CacheKey.PAY_MAP, CacheKey.PAYMENT_HASH_KEY + taskId, payment);
                //删除任务信息缓存
                redis.deleteMap(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId);
                log.info("[" + task.getTaskId() + "]任务执行完毕!");
            } else if (task.isNeedRetry()) {//触发任务重试
                if (task.canRetry()) {
                    log.warn("[" + task.getTaskId() + "]任务执行失败!!");
                    retry(payTask); //重试
                } else {
                    //状态码
                    task.setStatusCode(TaskStatus.EXECUTE_FAILED.getCode());
                    //提示信息
                    task.setMessage(TaskStatus.EXECUTE_FAILED.getMessage());
                    //将最终的任务状态更新到mongo中，没有此任务信息，会自动创建
                    payService.savePayTask(payTask);
                    //删除任务信息缓存
                    redis.deleteMap(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId);
                    log.error("[" + task.getTaskId() + "]重试次数已达上限，任务执行失败!!!");
                }
            }
        });
    }

    @Override
    public PayTask retry(PayTask task) {
        return payService.retry(task.getTaskId()); //重试
    }
}
