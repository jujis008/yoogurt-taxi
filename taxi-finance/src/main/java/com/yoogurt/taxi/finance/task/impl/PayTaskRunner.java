package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.service.PayChannelService;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.finance.task.TaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("payTaskRunner")
public class PayTaskRunner implements TaskRunner<PayTask> {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PayService payService;

    @Autowired
    private RedisHelper redis;

    /**
     * 通过第三方交易平台，获取一个支付对象
     *
     * @param payTask 支付任务
     */
    @Override
    public void run(final PayTask payTask) {
        PayChannel channel = PayChannel.getChannelByName(payTask.getPayParams().getChannel());
        if (channel == null) return;

        PayChannelService payChannelService = (PayChannelService) context.getBean(channel.getServiceName());
        CompletableFuture<ResponseObj> future = payChannelService.doTask(payTask);
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
