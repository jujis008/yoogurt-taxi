package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.TaskStatus;
import com.yoogurt.taxi.finance.service.AlipayService;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskRunner;
import com.yoogurt.taxi.finance.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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
        future.thenAccept(obj -> {

            if (obj.isSuccess()) {
                final String taskId = payTask.getTaskId();
                TaskInfo task = payTask.getTask();
                Payment payment = (Payment) obj.getBody();
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
        });
    }

}
