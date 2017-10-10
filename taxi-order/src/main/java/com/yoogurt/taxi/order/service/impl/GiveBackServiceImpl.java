package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderGiveBackInfo;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.GiveBackOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.GiveBackDao;
import com.yoogurt.taxi.order.form.GiveBackForm;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import com.yoogurt.taxi.order.service.GiveBackService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("giveBackService")
public class GiveBackServiceImpl implements GiveBackService {

    @Autowired
    private GiveBackDao giveBackDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private GiveBackRuleService ruleService;

    @Override
    public GiveBackOrderModel doGiveBack(GiveBackForm giveBackForm) {
        Long orderId = giveBackForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待还车】
        if(!OrderStatus.GIVE_BACK.equals(status)) return null;

        OrderGiveBackInfo giveBackInfo = new OrderGiveBackInfo();
        BeanUtils.copyProperties(giveBackForm, giveBackInfo);
        giveBackInfo.setRuleId(0L);
        giveBackInfo.setIsDisobey(Boolean.FALSE);
        giveBackInfo.setUnit("MINUTES");
        giveBackInfo.setTime(0);

        //超过了交车时间，需要计算违约金
        Date now = new Date();
        //实际的交车时间超出了预约的交车时间
        if (orderInfo.getHandoverTime().before(now)) {
            //计算时间，向上取整
            int minutes = (int) Math.floor((now.getTime() - orderInfo.getHandoverTime().getTime()) / 60000.00);
            String unit = "MINUTES";
            OrderGiveBackRule rule = ruleService.getRuleInfo(minutes, unit);
            giveBackInfo.setIsDisobey(rule != null);
            if (rule != null) {
                giveBackInfo.setRuleId(rule.getRuleId());
                giveBackInfo.setUnit(unit);
                giveBackInfo.setTime(minutes);
                //计算违约金
                giveBackInfo.setFineMoney(ruleService.calculate(rule, minutes).getAmount());
            } else {
                giveBackInfo.setTime(minutes);
            }
        }
        if (giveBackDao.insertSelective(giveBackInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            return (GiveBackOrderModel) info(orderId);
        }
        return null;
    }

    @Override
    public OrderGiveBackInfo getGiveBackInfo(Long orderId) {
        return giveBackDao.selectById(orderId);
    }

    @Override
    public OrderModel info(Long orderId) {
        GiveBackOrderModel model = new GiveBackOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderGiveBackInfo giveBackInfo = getGiveBackInfo(orderId);
        BeanUtils.copyProperties(giveBackInfo, model);
        //实际还车时间
        model.setRealGiveBackTime(giveBackInfo.getGmtCreate());
        model.setGiveBackLat(giveBackInfo.getLat());
        model.setGiveBackLng(giveBackInfo.getLng());

        return model;
    }
}
