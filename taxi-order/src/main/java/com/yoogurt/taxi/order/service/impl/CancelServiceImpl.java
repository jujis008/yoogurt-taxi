package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.CancelDao;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service("cancelService")
public class CancelServiceImpl extends AbstractOrderBizService implements CancelService {

    @Autowired
    private CancelDao cancelDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private CancelRuleService ruleService;

    @Autowired
    private DisobeyService disobeyService;

    @Transactional
    @Override
    public CancelOrderModel doCancel(CancelForm cancelForm) {
        Long orderId = cancelForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, cancelForm.getUserId());
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //已完成的订单不可取消了
        if (OrderStatus.FINISH.equals(status)) return null;

        OrderCancelInfo cancelInfo = new OrderCancelInfo();
        BeanUtils.copyProperties(cancelForm, cancelInfo);
        ResponsibleParty responsibleParty = ResponsibleParty.getEnumsByCode(cancelForm.getResponsibleParty());
        UserType userType = UserType.getEnumsByCode(responsibleParty.getUserType());
        //默认时间单位
        String unit = "HOURS";

        cancelInfo.setRuleId(0L);
        cancelInfo.setUnit(unit);
        cancelInfo.setIsDisobey(!ResponsibleParty.NONE.equals(responsibleParty));
        cancelInfo.setTime(0);

        //App端提交的取消申请
        if (cancelForm.isFromApp()) {
            //超过了交车时间，需要计算违约金
            Date now = new Date();
            //计算时间，向上取整
            int hours = (int) Math.floor((now.getTime() - orderInfo.getHandoverTime().getTime()) / 3600000.00);
            OrderCancelRule rule = ruleService.getRuleInfo(hours, unit);
            if (rule != null) {
                //该时段不允许取消
                if (!rule.getAllowCancel()) return null;
                cancelInfo.setRuleId(rule.getRuleId());
                cancelInfo.setUnit(unit);
                cancelInfo.setTime(hours);
                //计算违约金
                BigDecimal fineMoney = ruleService.calculate(rule, orderInfo.getAmount()).getAmount();
                cancelInfo.setFineMoney(fineMoney);

                //违约记录
                String description = "距离交车时间" + hours + "个小时取消订单，缴纳违约金￥" + fineMoney.doubleValue();
                OrderDisobeyInfo disobey = disobeyService.buildDisobeyInfo(
                        orderInfo, userType, DisobeyType.CANCEL_ORDER,
                        rule.getRuleId(), fineMoney, description);
                disobeyService.addDisobey(disobey);
            } else {
                cancelInfo.setTime(hours);
            }
        }
        if (cancelDao.insertSelective(cancelInfo) == 1) {
            //修改租单发布状态-已取消
            rentInfoService.modifyStatus(orderInfo.getRentId(), RentStatus.CANCELED);
            //修改订单状态
            orderInfoService.modifyStatus(orderId, OrderStatus.CANCELED);
            //已取消，操作方不需要提醒
            if (cancelForm.isFromApp()) {
                //对于App客户端，操作者通常是责任方，除非无责取消
                super.push(orderInfo, userType, SendType.ORDER_CANCEL);
            }
            return (CancelOrderModel) info(orderId, cancelForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderCancelInfo getCancelInfo(Long orderId) {
        return cancelDao.selectById(orderId);
    }

    @Override
    public OrderModel info(Long orderId, Long userId) {
        CancelOrderModel model = new CancelOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) return null;

        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderCancelInfo cancelInfo = getCancelInfo(orderId);
        BeanUtils.copyProperties(cancelInfo, model);
        model.setCancelTime(cancelInfo.getGmtCreate());
        return model;
    }
}
