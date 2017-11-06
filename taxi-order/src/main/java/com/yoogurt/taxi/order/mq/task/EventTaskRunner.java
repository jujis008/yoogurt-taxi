package com.yoogurt.taxi.order.mq.task;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.EventTask;
import com.yoogurt.taxi.finance.task.TaskRunner;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 执行回调任务
 */
@Slf4j
@Service("eventTaskRunner")
public class EventTaskRunner implements TaskRunner<EventTask> {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPaymentService paymentService;

    @Autowired
    private PayService payService;

    /**
     * 通过第三方交易平台，执行特定的任务
     *
     * @param eventTask 任务信息
     */
    @Override
    public void run(EventTask eventTask) {
        if(eventTask == null) return;
        final Event event = eventTask.getEvent();
        log.info("[taxi-order#" + eventTask.getTaskId() + "]" + event.getEventType());
        Notify notify = event.getData();
        PayChannel payChannel = PayChannel.getChannelByName(notify.getChannel());
        if(payChannel == null || StringUtils.isBlank(payChannel.getServiceName())) return;
        Money paidMoney = new Money(notify.getAmount());
        String orderNo = notify.getOrderNo();
        if(StringUtils.isBlank(orderNo)) return;
        /********************  更新订单的支付状态  ********************************/
        OrderInfo orderInfo = orderInfoService.getOrderInfo(Long.valueOf(orderNo), null);
        if(orderInfo == null || !paidMoney.getAmount().equals(orderInfo.getAmount())) return;
        orderInfo.setIsPaid(true);
        orderInfoService.saveOrderInfo(orderInfo, false);
        /********************  更新订单的支付状态  The End  ***********************/

        /********************  插入订单的支付记录  ********************************/
        Payment payInfo = eventTask.getPayment();
        OrderPayment payment = new OrderPayment(payInfo.getPayId(), Long.valueOf(orderNo));
        payment.setSubject(payInfo.getSubject());
        payment.setBody(payInfo.getBody());
        payment.setOrderId(Long.valueOf(orderNo));
        payment.setPayChannel(payChannel.getName());
        payment.setTransactionNo(notify.getTransactionNo());
        payment.setStatus(20); //支付完成
        payment.setPaidTime(new Date(notify.getNotifyTimestamp()));//支付完成时间
        paymentService.addPayment(payment);
        /********************  插入订单的支付记录  The End***********************/

        /********************  更新payment对象  *******************************/
        payInfo.setPaid(true);
        payInfo.setPaidTime(notify.getNotifyTimestamp());
        payInfo.setPaidAmount(paidMoney.getCent());
        payService.updatePayment(payInfo);
        /********************  更新payment对象  The End***********************/
    }

    @Override
    public EventTask retry(EventTask task) {
        return null;
    }
}