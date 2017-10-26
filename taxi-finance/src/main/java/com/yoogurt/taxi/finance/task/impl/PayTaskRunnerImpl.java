package com.yoogurt.taxi.finance.task.impl;

import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import com.yoogurt.taxi.finance.task.PayTaskRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayTaskRunnerImpl implements PayTaskRunner {

    @Autowired
    private PayService payService;

    @Autowired
    private RedisHelper redis;

    /**
     * 通过第三方交易平台，获取一个支付对象
     *
     * @param task 支付任务
     * @return 支付对象
     */
    @Override
    public Payment run(PayTask task) {

        Payment payment = payService.buildPayment(task.getPayParams());
        if(payment == null) return null;
        return payment;
    }

}
