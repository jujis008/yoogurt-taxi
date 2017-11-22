package com.yoogurt.taxi.account.mq.task;

import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.rest.RestFinanceService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.bo.BaseNotify;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.vo.PaymentVo;
import com.yoogurt.taxi.pay.doc.Event;
import com.yoogurt.taxi.pay.doc.EventTask;
import com.yoogurt.taxi.pay.runner.impl.AbstractEventTaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ChargeNotifyTaskRunner extends AbstractEventTaskRunner {

    @Autowired
    private FinanceBillService financeBillService;

    @Autowired
    private RestFinanceService financeService;

    @Override
    public CompletableFuture<ResponseObj> doTask(EventTask eventTask) {

        return CompletableFuture.supplyAsync(() -> notify(eventTask));
    }

    public ResponseObj notify(EventTask eventTask){
        if (eventTask == null) {
            return ResponseObj.fail();
        }
        final Event event = eventTask.getEvent();
        log.info("[taxi-account#" + eventTask.getTaskId() + "]" + event.getEventType());
        log.info("+++++++++++++++++充值回调的业务处理开始++++++++++++++");
        BaseNotify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if (payChannel == null || !payChannel.isThirdParty()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "支付渠道暂未开通");
        }
        log.info(payChannel.getDetail());
        String orderNo = notify.getOrderNo();
        if (StringUtils.isBlank(orderNo)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "无效的账单号");
        }
        FinanceBill financeBill = financeBillService.getFinanceBillByBillNo(orderNo);
        if (financeBill == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "交易账单不存在");
        }
        Money paidMoney = new Money(notify.getAmount());
        if (!new Money(financeBill.getAmount()).equals(paidMoney)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "支付金额不吻合");
        }
        if (financeBillService.chargeSuccessOrFailure(orderNo, BillStatus.SUCCESS) <= 0) {

            log.error("充值回调的业务处理失败："+orderNo);
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "账单更新失败");
        }

        log.info("充值回调的业务处理成功："+orderNo);
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
        return ResponseObj.success();
    }
}
