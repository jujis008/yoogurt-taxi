package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.service.AlipayService;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskRunner;
import com.yoogurt.taxi.finance.task.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PayTaskRunnerImpl implements PayTaskRunner {

    @Autowired
    private PayService payService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private RedisHelper redis;

    /**
     * 通过第三方交易平台，获取一个支付对象
     *
     * @param payTask 支付任务
     */
    @Override
    public void run(final PayTask payTask) {

        CompletableFuture<ResponseObj> future = alipayService.doTask(payTask);
        if(future == null) return;
        future.thenAccept(obj -> {

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
                log.info("任务执行完毕！！");
            } else if (task.isNeedRetry()) {//触发任务重试
                if (Constants.MAX_PAY_TASK_RETRY_TIMES >= task.getRetryTimes()) {
                    //记录重试操作
                    payTask.retryRecord();
                    //重新设置任务信息缓存
                    redis.put(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId, payTask);
                    //重试
                    payService.retry(taskId);
                    log.warn("任务执行失败，正在重试！！");
                } else {
                    //将最终的任务状态更新到mongo中，没有此任务信息，会自动创建
                    payService.savePayTask(payTask);
                    //删除任务信息缓存
                    redis.deleteMap(CacheKey.PAY_MAP, CacheKey.TASK_HASH_KEY + taskId);
                    log.warn("任务执行失败！！");
                }
            }
        });
    }

}
