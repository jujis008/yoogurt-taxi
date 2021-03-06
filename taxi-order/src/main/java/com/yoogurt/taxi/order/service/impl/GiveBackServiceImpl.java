package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderGiveBackInfo;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.DisobeyType;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.GiveBackOrderModelBase;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.GiveBackDao;
import com.yoogurt.taxi.order.form.GiveBackForm;
import com.yoogurt.taxi.order.service.DisobeyService;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import com.yoogurt.taxi.order.service.GiveBackService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Service("giveBackService")
public class GiveBackServiceImpl extends AbstractOrderBizService implements GiveBackService {

    @Autowired
    private GiveBackDao giveBackDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private GiveBackRuleService ruleService;

    @Autowired
    private DisobeyService disobeyService;

    @Autowired
    private RedisHelper redisHelper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public GiveBackOrderModelBase doGiveBack(GiveBackForm giveBackForm) {
        String orderId = giveBackForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, giveBackForm.getUserId());
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待还车】
        if (!OrderStatus.GIVE_BACK.equals(status)) {
            return null;
        }

        OrderGiveBackInfo giveBackInfo = new OrderGiveBackInfo();
        BeanUtils.copyProperties(giveBackForm, giveBackInfo);
        giveBackInfo.setRuleId(0L);
        giveBackInfo.setIsDisobey(Boolean.FALSE);
        giveBackInfo.setUnit("MINUTES");
        giveBackInfo.setTime(0);

        //超过了交车时间，需要计算违约金
        Date now = new Date();
        //实际的交车时间超出了预约的交车时间
        if (orderInfo.getGiveBackTime().before(now)) {
            //计算时间，向上取整
            int minutes = (int) Math.floor((now.getTime() - orderInfo.getGiveBackTime().getTime()) / 60000.00);
            String unit = "MINUTES";
            OrderGiveBackRule rule = ruleService.getRuleInfo(now.getTime() - orderInfo.getGiveBackTime().getTime());
            giveBackInfo.setIsDisobey(rule != null);
            if (rule != null) {
                giveBackInfo.setRuleId(rule.getRuleId());
                giveBackInfo.setUnit(unit);
                giveBackInfo.setTime(minutes);
                //计算违约金
                BigDecimal fineMoney = ruleService.calculate(rule, minutes, orderInfo.getAmount()).getAmount();
                giveBackInfo.setFineMoney(fineMoney);
                //违约记录
                String description = "还车超时" + minutes + "分钟，缴纳违约金￥" + fineMoney.doubleValue();
                OrderDisobeyInfo disobey = disobeyService.buildDisobeyInfo(
                        orderInfo, UserType.USER_APP_AGENT, DisobeyType.AGENT_DRIVER_HANDOVER_TIMEOUT,
                        rule.getRuleId(), fineMoney, description);
                disobeyService.addDisobey(disobey);
            } else {
                giveBackInfo.setTime(minutes);
            }
        }
        if (giveBackDao.insertSelective(giveBackInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());

            //清除还车提醒任务
            redisHelper.del(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER_KEY + orderId);
            redisHelper.del(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER1_KEY + orderId);

            //已还车，通知车主
            super.push(orderInfo, UserType.USER_APP_OFFICE, SendType.ORDER_GIVE_BACK, new HashMap<>(1));
            return (GiveBackOrderModelBase) info(orderId, giveBackForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderGiveBackInfo getGiveBackInfo(String orderId) {
        return giveBackDao.selectById(orderId);
    }

    @Override
    public OrderModel info(String orderId, String userId) {
        GiveBackOrderModelBase model = new GiveBackOrderModelBase();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) {
            return null;
        }

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
