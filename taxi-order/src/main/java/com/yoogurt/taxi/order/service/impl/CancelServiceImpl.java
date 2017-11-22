package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.order.CancelOrderModelBase;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.vo.ModificationVo;
import com.yoogurt.taxi.order.dao.CancelDao;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.service.*;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

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

    @Autowired
    private RestAccountService restAccountService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CancelOrderModelBase doCancel(CancelForm cancelForm) {
        String orderId = cancelForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, null);
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //已完成的订单不可取消了
        if (OrderStatus.FINISH.equals(status)) {
            return null;
        }
        //已取消的订单不可操作了
        if (OrderStatus.CANCELED.equals(status)) {
            return null;
        }

        OrderCancelInfo cancelInfo = new OrderCancelInfo();
        BeanUtils.copyProperties(cancelForm, cancelInfo);
        ResponsibleParty responsibleParty = ResponsibleParty.getEnumsByCode(cancelForm.getResponsibleParty());
        UserType userType = UserType.getEnumsByCode(cancelForm.getUserType());
        //默认时间单位
        String unit = "HOURS";

        cancelInfo.setRuleId(0L);
        cancelInfo.setUnit(unit);
        cancelInfo.setIsDisobey(!ResponsibleParty.NONE.equals(responsibleParty));
        cancelInfo.setTime(0);

        Date now = new Date();
        //计算时间，向上取整
        int hours = (int) Math.abs(Math.floor((orderInfo.getHandoverTime().getTime() - now.getTime()) / 3600000.00));

        if (cancelForm.isInternal()) {
            //若司机已支付，需要退款
            if (orderInfo.getIsPaid()) {
                //退款到司机账户，记录为平台支出
                ModificationVo vo = ModificationVo.builder().contextId(orderInfo.getOrderId())
                        .userId(orderInfo.getAgentUserId())
                        .outUserId("0")
                        .inUserId(orderInfo.getAgentUserId())
                        .money(orderInfo.getAmount())
                        .payment(Payment.PLATFORM.getCode())
                        .type(TradeType.REFUND.getCode()).build();
                restAccountService.updateAccount(vo);
            }
            //添加一条车主的违约记录
            BigDecimal fineMoney = orderInfo.getAmount();
            cancelInfo.setRuleId(0L);
            cancelInfo.setUnit(unit);
            cancelInfo.setTime(hours);
            cancelInfo.setFineMoney(fineMoney);
            String description = "车主未按时交车，系统自动取消订单，车主缴纳违约金￥" + fineMoney.doubleValue();
            OrderDisobeyInfo disobey = disobeyService.buildDisobeyInfo(
                    orderInfo, UserType.getEnumsByCode(responsibleParty.getUserType()), DisobeyType.OFFICIAL_DRIVER_HANDOVER_TIMEOUT,
                    0L, fineMoney, description);
            disobeyService.addDisobey(disobey);
        } else if (cancelForm.isFromApp()) {
            //App端提交的取消申请
            //超过了交车时间，需要计算违约金

            OrderCancelRule rule = ruleService.getRuleInfo(orderInfo.getHandoverTime().getTime() - now.getTime());
            if (rule != null) {
                //该时段不允许取消
                if (!rule.getAllowCancel()) {
                    return null;
                }
                cancelInfo.setRuleId(rule.getRuleId());
                cancelInfo.setUnit(unit);
                cancelInfo.setTime(hours);
                //计算违约金
                BigDecimal fineMoney = ruleService.calculate(rule, orderInfo.getAmount()).getAmount();
                cancelInfo.setFineMoney(fineMoney);

                //违约记录
                String description = "距离交车时间" + hours + "个小时取消订单，缴纳违约金￥" + fineMoney.doubleValue();
                OrderDisobeyInfo disobey = disobeyService.buildDisobeyInfo(
                        orderInfo, UserType.getEnumsByCode(responsibleParty.getUserType()), DisobeyType.CANCEL_ORDER,
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
            if (cancelForm.isInternal()) {
                //超时自动取消，需要通知双方
                super.push(orderInfo, UserType.USER_APP_OFFICE, SendType.ORDER_HANDOVER_UNFINISHED_REMINDER, new HashMap<>(1));
                super.push(orderInfo, UserType.USER_APP_AGENT, SendType.ORDER_HANDOVER_UNFINISHED_REMINDER, new HashMap<>(1));
            } else if (cancelForm.isFromApp()) {
                //对于App客户端，操作者通常是责任方，除非无责取消
                //通知对方，订单已取消
                super.push(orderInfo, userType.equals(UserType.USER_APP_AGENT) ? UserType.USER_APP_OFFICE : UserType.USER_APP_AGENT, SendType.ORDER_CANCEL, new HashMap<>(1));
            } else {
                //后台取消，需要通知双方
                super.push(orderInfo, UserType.USER_APP_OFFICE, SendType.ORDER_CANCEL, new HashMap<>(1));
                super.push(orderInfo, UserType.USER_APP_AGENT, SendType.ORDER_CANCEL, new HashMap<>(1));
            }
            return (CancelOrderModelBase) info(orderId, cancelForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderCancelInfo getCancelInfo(String orderId) {
        return cancelDao.selectById(orderId);
    }

    @Override
    public OrderModel info(String orderId, String userId) {
        CancelOrderModelBase model = new CancelOrderModelBase();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) {
            return null;
        }

        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderCancelInfo cancelInfo = getCancelInfo(orderId);
        BeanUtils.copyProperties(cancelInfo, model);
        model.setCancelTime(cancelInfo.getGmtCreate());
        return model;
    }
}
