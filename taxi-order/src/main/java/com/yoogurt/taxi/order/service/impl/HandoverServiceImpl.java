package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.order.HandoverOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.vo.ModificationVo;
import com.yoogurt.taxi.order.dao.HandoverDao;
import com.yoogurt.taxi.order.form.HandoverForm;
import com.yoogurt.taxi.order.service.*;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("handoverService")
public class HandoverServiceImpl extends AbstractOrderBizService implements HandoverService {

    @Autowired
    private HandoverDao handoverDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private HandoverRuleService ruleService;

    @Autowired
    private DisobeyService disobeyService;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private OrderPaymentService paymentService;

    @Autowired
    private RestAccountService restAccountService;

    /**
     * 正式司机确认交车
     *
     * @param handoverForm 交车信息
     * @return 交车相关信息
     */
    @Transactional
    @Override
    public HandoverOrderModel doHandover(HandoverForm handoverForm) {
        Long orderId = handoverForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, handoverForm.getUserId());
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待交车】
        if (!OrderStatus.HAND_OVER.equals(status)) return null;

        OrderHandoverInfo handoverInfo = new OrderHandoverInfo();
        handoverInfo.setOrderId(orderId);
        handoverInfo.setRealHandoverAddress(handoverForm.getRealHandoverAddress());
        handoverInfo.setLat(handoverForm.getLat());
        handoverInfo.setLng(handoverForm.getLng());
        handoverInfo.setRuleId(0L);
        handoverInfo.setUnit("MINUTES");
        handoverInfo.setFineMoney(new BigDecimal(0));
        handoverInfo.setIsDisobey(Boolean.FALSE);
        handoverInfo.setTime(0);
        //超过了交车时间，需要计算违约金
        Date now = new Date();
        //实际的交车时间超出了预约的交车时间
        if (orderInfo.getHandoverTime().before(now)) {
            //计算时间，向上取整
            int minutes = (int) Math.abs(Math.floor((now.getTime() - orderInfo.getHandoverTime().getTime()) / 60000.00));
            String unit = "MINUTES";
            OrderHandoverRule rule = ruleService.getRuleInfo(now.getTime() - orderInfo.getHandoverTime().getTime());
            handoverInfo.setIsDisobey(rule != null);
            if (rule != null) {
                handoverInfo.setRuleId(rule.getRuleId());
                handoverInfo.setUnit(unit);
                handoverInfo.setTime(minutes);
                //计算违约金
                BigDecimal fineMoney = ruleService.calculate(rule, minutes, orderInfo.getAmount()).getAmount();
                handoverInfo.setFineMoney(fineMoney);
                String description = "交车超时" + minutes + "分钟，缴纳违约金￥" + fineMoney.doubleValue();
                OrderDisobeyInfo disobey = disobeyService.buildDisobeyInfo(
                        orderInfo, UserType.USER_APP_OFFICE, DisobeyType.OFFICIAL_DRIVER_HANDOVER_TIMEOUT,
                        rule.getRuleId(), fineMoney, description);
                if (disobey != null) disobeyService.addDisobey(disobey);
            } else {
                handoverInfo.setTime(minutes);
            }
        }
        if (handoverDao.insertSelective(handoverInfo) == 1) {
            if (!orderInfo.getIsPaid()) {//未支付
                //代理司机扣款，扣款顺序：余额》》押金
                ModificationVo vo = ModificationVo.builder().contextId(orderInfo.getOrderId())
                        .userId(orderInfo.getAgentUserId())
                        .outUserId(orderInfo.getAgentUserId())
                        .inUserId(orderInfo.getOfficialUserId())
                        .money(orderInfo.getAmount())
                        .payment(Payment.BALANCE.getCode())
                        .type(TradeType.OUTCOME.getCode()).build();
                restAccountService.updateAccount(vo);

                orderInfoService.modifyPayStatus(orderId);//订单修改成已支付
            }
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());

            //取消交车提醒相关任务
            redisHelper.del(CacheKey.MESSAGE_ORDER_HANDOVER_UNFINISHED_REMINDER_KEY + orderId);
            redisHelper.del(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER_KEY + orderId);
            redisHelper.del(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER1_KEY + orderId);

            //向代理司机发送已交车的通知
            super.push(orderInfo, UserType.USER_APP_AGENT, SendType.ORDER_HANDOVER, new HashMap<>());
            return (HandoverOrderModel) info(orderId, handoverForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderModel info(Long orderId, Long userId) {
        HandoverOrderModel model = new HandoverOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) return null;

        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderHandoverInfo handoverInfo = getHandoverInfo(orderId);
        BeanUtils.copyProperties(handoverInfo, model);
        //实际交车经纬度
        model.setHandoverLat(handoverInfo.getLat());
        model.setHandoverLng(handoverInfo.getLng());
        //实际交车时间
        model.setRealHandoverTime(handoverInfo.getGmtCreate());

        return model;
    }

    @Override
    public OrderHandoverInfo getHandoverInfo(Long orderId) {
        return handoverDao.selectById(orderId);
    }

}
