package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.CancelDao;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.service.CancelRuleService;
import com.yoogurt.taxi.order.service.CancelService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service("cancelService")
public class CancelServiceImpl implements CancelService {

    @Autowired
    private CancelDao cancelDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CancelRuleService ruleService;

    @Override
    public CancelOrderModel doCancel(CancelForm cancelForm) {
        Long orderId = cancelForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //已完成的订单不可取消了
        if(OrderStatus.FINISH.equals(status)) return null;

        OrderCancelInfo cancelInfo = new OrderCancelInfo();
        BeanUtils.copyProperties(cancelForm, cancelInfo);
        //默认时间单位
        String unit = "HOURS";

        cancelInfo.setRuleId(0L);
        cancelInfo.setUnit(unit);
        cancelInfo.setFineMoney(new BigDecimal(0));
        cancelInfo.setIsDisobey(Boolean.FALSE);
        cancelInfo.setTime(0);
        //超过了交车时间，需要计算违约金
        Date now = new Date();
        //计算时间，向上取整
        int hours = (int) Math.floor((now.getTime() - orderInfo.getHandoverTime().getTime()) / 3600000.00);
        OrderCancelRule rule = ruleService.getRuleInfo(hours, unit);
        cancelInfo.setIsDisobey(rule != null);
        if (rule != null) {
            cancelInfo.setRuleId(rule.getRuleId());
            cancelInfo.setUnit(unit);
            cancelInfo.setTime(hours);
            //计算违约金
            cancelInfo.setFineMoney(ruleService.calculate(rule, orderInfo.getAmount()).getAmount());
        } else {
            cancelInfo.setTime(hours);
        }
        if (cancelDao.insertSelective(cancelInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            return (CancelOrderModel) info(orderId);
        }
        return null;
    }

    @Override
    public OrderCancelInfo getCancelInfo(Long orderId) {
        return cancelDao.selectById(orderId);
    }

    @Override
    public OrderModel info(Long orderId) {
        return null;
    }
}
