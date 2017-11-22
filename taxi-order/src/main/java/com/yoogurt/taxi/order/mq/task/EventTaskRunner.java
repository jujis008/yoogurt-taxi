package com.yoogurt.taxi.order.mq.task;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.dal.bo.BaseNotify;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.vo.PaymentVo;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import com.yoogurt.taxi.order.service.rest.RestFinanceService;
import com.yoogurt.taxi.pay.doc.Event;
import com.yoogurt.taxi.pay.doc.EventTask;
import com.yoogurt.taxi.pay.doc.Payment;
import com.yoogurt.taxi.pay.runner.impl.AbstractEventTaskRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 执行回调任务
 */
@Slf4j
@Service("eventTaskRunner")
public class EventTaskRunner extends AbstractEventTaskRunner {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPaymentService paymentService;

    @Autowired
    private RestFinanceService restFinanceService;

    @Override
    public CompletableFuture<ResponseObj> doTask(final EventTask eventTask) {

        return CompletableFuture.supplyAsync(() -> notify(eventTask));
    }

    /**
     * 通过第三方交易平台，执行特定的任务
     *
     * @param eventTask 任务信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseObj notify(EventTask eventTask) {
        if (eventTask == null) {
            return ResponseObj.fail();
        }
        final Event event = eventTask.getEvent();
        log.info("[taxi-order#" + eventTask.getTaskId() + "]" + event.getEventType());
        BaseNotify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if (payChannel == null || !payChannel.isThirdParty()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "支付渠道暂未开通");
        }
        Money paidMoney = new Money(notify.getAmount());
        String orderNo = notify.getOrderNo();
        if (StringUtils.isBlank(orderNo)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "无效的订单号");
        }
        /********************  更新订单的支付状态  ********************************/
        synchronized (orderNo.intern()) {
            OrderInfo orderInfo = orderInfoService.getOrderInfo(orderNo, null);
            if(orderInfo == null) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单不存在");
            }
            if(orderInfo.getIsPaid()) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单已支付");
            }
            if (!paidMoney.getAmount().equals(orderInfo.getAmount())) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "支付金额不吻合");
            }
            orderInfo.setIsPaid(true);
            if (orderInfoService.saveOrderInfo(orderInfo, false) == null) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单状态更改失败");
            }
            log.info("更改订单支付状态成功！");
            /********************  更新订单的支付状态  The End  ***********************/

            /********************  操作订单的支付记录  ********************************/
            Payment payInfo = eventTask.getPayment();
            List<OrderPayment> payments = paymentService.getPayments(orderNo, 10);
            //没有支付记录，则主动创建一条
            if (CollectionUtils.isEmpty(payments)) {
                OrderPayment payment = new OrderPayment(payInfo.getPayId(), orderNo);
                payment.setSubject(payInfo.getSubject());
                payment.setBody(payInfo.getBody());
                payment.setOrderId(orderNo);
                payment.setPayChannel(payChannel.getName());
                payment.setTransactionNo(notify.getTransactionNo());
                //支付完成
                payment.setStatus(20);
                payment.setAmount(new Money(notify.getAmount()).getAmount());
                //支付完成时间
                payment.setPaidTime(new Date(notify.getNotifyTimestamp()));
                if (paymentService.addPayment(payment) == null) {
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单支付记录插入失败");
                }
            } else {
                //有支付记录，更新第一条记录
                OrderPayment payment = payments.get(0);
                payment.setTransactionNo(notify.getTransactionNo());
                //支付完成
                payment.setStatus(20);
                //支付完成时间
                payment.setPaidTime(new Date(notify.getNotifyTimestamp()));
                if (paymentService.modifyPayment(payment) == null) {
                    return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单支付记录更改失败");
                }
            }
            log.info("订单支付记录操作成功！");
            /********************  操作订单的支付记录  The End***********************/

            /********************  更新payment对象  *******************************/
            try {
                restFinanceService.updatePayment(PaymentVo.builder()
                        .payId(payInfo.getPayId())
                        .paidAmount(paidMoney.getCent())
                        .paidTime(notify.getNotifyTimestamp())
                        .transactionNo(notify.getTransactionNo())
                        .build());
            } catch (Exception e) {
                log.error("更新支付对象异常, {}", e);
            }
            /********************  更新payment对象  The End***********************/
        }
        return ResponseObj.success();
    }

}
