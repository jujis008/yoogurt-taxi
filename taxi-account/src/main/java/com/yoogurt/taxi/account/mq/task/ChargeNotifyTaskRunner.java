package com.yoogurt.taxi.account.mq.task;

import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.condition.account.BillCondition;
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
        log.info("+++++++++++++++++充值回调的业务处理开始++++++++++++++");
        Notify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if (payChannel == null || StringUtils.isBlank(payChannel.getServiceName())) return;
        log.info(payChannel.getDetail());
        String orderNo = notify.getOrderNo();
        FinanceBill financeBill = financeBillService.getFinanceBillByBillNo(Long.valueOf(orderNo));
        Money paidMoney = new Money(notify.getAmount());
        if (financeBill == null) {
            return;
        }
        if (!new Money(financeBill.getAmount()).equals(paidMoney)) {
            return;
        }
        if (StringUtils.isBlank(orderNo)) return;
        int i = financeBillService.chargeSuccessOrFailure(Long.valueOf(orderNo), BillStatus.SUCCESS);
        if (i>0) {
            log.info("+++++++++++++++++充值回调的业务处理成功："+orderNo+"++++++++++++++");
        } else {
            log.error("+++++++++++++++++充值回调的业务处理失败："+orderNo+"++++++++++++++");
        }
    }
}
