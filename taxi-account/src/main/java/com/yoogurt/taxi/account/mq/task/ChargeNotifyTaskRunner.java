package com.yoogurt.taxi.account.mq.task;

import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.PayChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChargeNotifyTaskRunner {

    @Autowired
    private FinanceBillService financeBillService;

    public void run(EventTask eventTask){
        if (eventTask == null) return;
        final Event event = eventTask.getEvent();
        log.info("[taxi-account#" + eventTask.getTaskId() + "]" + event.getEventType());
        log.info("+++++++++++++++++开始充值回调的业务处理++++++++++++++");
        Notify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if (payChannel == null || StringUtils.isBlank(payChannel.getServiceName())) return;
        Money paidMoney = new Money(notify.getAmount());
        String orderNo = notify.getOrderNo();
        if (StringUtils.isBlank(orderNo)) return;
        financeBillService.chargeSuccessOrFailure(Long.valueOf(orderNo), BillStatus.SUCCESS);
    }
}
