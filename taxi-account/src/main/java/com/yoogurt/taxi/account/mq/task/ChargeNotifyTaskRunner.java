package com.yoogurt.taxi.account.mq.task;

import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.rest.RestFinanceService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.condition.account.BillCondition;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.vo.PaymentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChargeNotifyTaskRunner {

    @Autowired
    private FinanceBillService financeBillService;

    @Autowired
    private RestFinanceService financeService;

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
        FinanceBill financeBill = financeBillService.getFinanceBillByBillNo(orderNo);
        Money paidMoney = new Money(notify.getAmount());
        if (financeBill == null) {
            return;
        }
        if (!new Money(financeBill.getAmount()).equals(paidMoney)) {
            return;
        }
        if (StringUtils.isBlank(orderNo)) return;
        int i = financeBillService.chargeSuccessOrFailure(orderNo, BillStatus.SUCCESS);
        if (i>0) {
            /********************  更新payment对象  *******************************/
            try {
                financeService.updatePayment(PaymentVo.builder()
                        .payId(eventTask.getPayment().getPayId())
                        .paidAmount(paidMoney.getCent())
                        .paidTime(notify.getNotifyTimestamp())
                        .transactionNo(notify.getTransactionNo())
                        .build());
            } catch (Exception e) {
                log.error("更新支付对象异常, {}", e);
            }
            /********************  更新payment对象  The End***********************/
            log.info("+++++++++++++++++充值回调的业务处理成功："+orderNo+"++++++++++++++");
        } else {
            log.error("+++++++++++++++++充值回调的业务处理失败："+orderNo+"++++++++++++++");
        }
    }
}
