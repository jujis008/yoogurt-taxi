package com.yoogurt.taxi.finance.mq.task;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.runner.impl.AbstractPayTaskRunner;
import com.yoogurt.taxi.pay.service.PayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("payTaskRunner")
public class PayTaskRunner extends AbstractPayTaskRunner {

    @Autowired
    private ApplicationContext context;

    @Override
    public CompletableFuture<ResponseObj> doTask(PayTask payTask) {
        PayChannel channel = PayChannel.getChannelByName(payTask.getPayParams().getChannel());
        if (channel == null) return null;

        PayChannelService payChannelService = (PayChannelService) context.getBean(channel.getServiceName());
        return payChannelService.doTask(payTask);
    }
}
