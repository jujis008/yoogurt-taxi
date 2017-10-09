package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderHandoverInfo;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.HandoverOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.HandoverDao;
import com.yoogurt.taxi.order.form.HandoverForm;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import com.yoogurt.taxi.order.service.HandoverService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service("handoverService")
public class HandoverServiceImpl implements HandoverService {

    @Autowired
    private HandoverDao handoverDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private HandoverRuleService ruleService;

    /**
     * 正式司机确认交车
     * @param handoverForm 交车信息
     * @return 交车相关信息
     */
    @Transactional
    @Override
    public HandoverOrderModel doHandover(HandoverForm handoverForm) {
        Long orderId = handoverForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【带交车】
        if(!OrderStatus.HAND_OVER.equals(status)) return null;

        OrderHandoverInfo handoverInfo = new OrderHandoverInfo();
        handoverInfo.setOrderId(orderId);
        handoverInfo.setRealHandoverAddress(handoverForm.getRealHandoverAddress());
        handoverInfo.setLat(handoverForm.getLat());
        handoverInfo.setLng(handoverForm.getLng());
        handoverInfo.setRuleId(0L);
        handoverInfo.setUnit("MINUTES");
        handoverInfo.setFineMoney(new BigDecimal(0));
        handoverInfo.setTime(0);
        //超过了交车时间，需要计算违约金
        Date now = new Date();
        //实际的交车时间超出了预约的交车时间
        if (orderInfo.getHandoverTime().before(now)) {
            //计算时间，向上取整
            int minutes = (int) Math.floor((now.getTime() - orderInfo.getHandoverTime().getTime()) / 60000.00);
            String unit = "MINUTES";
            OrderHandoverRule rule = ruleService.getRuleInfo(minutes, unit);
            handoverInfo.setIsDisobey(rule != null);
            if (rule != null) {
                handoverInfo.setRuleId(rule.getRuleId());
                handoverInfo.setUnit(unit);
                handoverInfo.setTime(minutes);
                //计算违约金
                handoverInfo.setFineMoney(ruleService.calculate(rule, minutes).getAmount());
            } else {
                handoverInfo.setTime(minutes);
            }
        }
        if (handoverDao.insertSelective(handoverInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            return (HandoverOrderModel) info(orderId);
        }
        return null;
    }

    @Override
    public OrderModel info(Long orderId) {
        HandoverOrderModel model = new HandoverOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
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
