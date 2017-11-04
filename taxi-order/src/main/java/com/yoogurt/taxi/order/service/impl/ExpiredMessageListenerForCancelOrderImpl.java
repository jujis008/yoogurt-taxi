package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.dal.enums.ResponsibleParty;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.mq.NotificationSender;
import com.yoogurt.taxi.order.service.CancelService;
import com.yoogurt.taxi.order.service.ExpiredMessageListener;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.RentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ExpiredMessageListenerForCancelOrderImpl implements ExpiredMessageListener {

    @Autowired
    private CancelService cancelService;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private NotificationSender sender;

    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody());
        log.info("[redis key expired] " + key);
        //租单超时取消任务
        if (key.startsWith(CacheKey.MESSAGE_ORDER_TIMEOUT_KEY)) {
            Long rentId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_TIMEOUT_KEY, ""));
            //取消订单
            RentInfo rentInfo = rentInfoService.cancelOverdue(rentId);
            if (rentInfo == null) {
                return;
            }
            log.info("[" + rentId + "]租单已超时，系统自动取消");

            //推送消息给发单人
            UserType userType = UserType.getEnumsByCode(rentInfo.getUserType());
            String title = userType == UserType.USER_APP_AGENT ? Constants.AGENT_APP_NAME : Constants.OFFICIAL_APP_NAME;
            SendType sendType = SendType.ORDER_TIMEOUT;
            String type = userType.equals(UserType.USER_APP_AGENT) ? "求租" : "出租";
            PushPayload payload = new PushPayload(userType, sendType, title);
            payload.setContent(String.format(sendType.getMessage(), type, rentId));
            payload.addUserId(rentInfo.getUserId());
            Map<String, Object> extras = new HashMap<>();
            extras.put("orderId", rentId);
            payload.setExtras(extras);
            sender.send(payload);
            return;
        }

        //订单交车前1小时通知任务，通知车主
        if (key.startsWith(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER1_KEY)) {
            Long rentId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER1_KEY, ""));
            log.info("[" + rentId + "]交车1小时前提醒");
            OrderInfo orderInfo = orderInfoService.getOrderInfo(rentId,null);
            if (orderInfo == null) return;
            SendType sendType = SendType.ORDER_HANDOVER_REMINDER1;
            UserType userType = UserType.USER_APP_OFFICE;
            String title = Constants.OFFICIAL_APP_NAME;
            PushPayload payload = new PushPayload(userType, sendType, title);
            payload.addUserId(orderInfo.getOfficialUserId());
            payload.setContent(String.format(sendType.getMessage(), rentId));
            Map<String, Object> extras = new HashMap<>();
            extras.put("orderId", rentId);
            payload.setExtras(extras);
            sender.send(payload);
            return;
        }

        //订单到交车时间点通知任务，通知车主
        if (key.startsWith(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER_KEY)) {
            Long rentId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER_KEY, ""));
            log.info("[" + rentId + "]交车提醒");
            OrderInfo orderInfo = orderInfoService.getOrderInfo(rentId,null);
            if (orderInfo == null) return;
            SendType sendType = SendType.ORDER_HANDOVER_REMINDER;
            UserType userType = UserType.USER_APP_OFFICE;
            String title = Constants.OFFICIAL_APP_NAME;
            PushPayload payload = new PushPayload(userType, sendType, title);
            payload.addUserId(orderInfo.getOfficialUserId());
            payload.setContent(String.format(sendType.getMessage(), rentId));
            Map<String, Object> extras = new HashMap<>();
            extras.put("orderId", rentId);
            payload.setExtras(extras);
            sender.send(payload);
            return;
        }

        //车主未交车，最大程度违约（租金用完）
        if (key.startsWith(CacheKey.MESSAGE_ORDER_HANDOVER_UNFINISHED_REMINDER_KEY)) {
            Long orderId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_HANDOVER_UNFINISHED_REMINDER_KEY, ""));
            log.info("[" + orderId + "]自动取消订单");
            CancelForm cancelForm = new CancelForm();
            cancelForm.setReason("车主未按预定时间交车，系统自动取消订单");
            cancelForm.setResponsibleParty(ResponsibleParty.OFFICIAL.getCode());
            cancelForm.setFromApp(false);
            cancelForm.setInternal(true);
            cancelForm.setOrderId(orderId);
            cancelForm.setUserType(UserType.SUPER_ADMIN.getCode());
            cancelForm.setUserId(0L);
            cancelService.doCancel(cancelForm);
            return;
        }

        //订单还车前一个小时，通知司机
        if (key.startsWith(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER1_KEY)) {
            Long orderId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER1_KEY, ""));
            log.info("[" + orderId + "]还车一小时前提醒");
            OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, null);
            if (orderInfo == null) return;
            SendType sendType = SendType.ORDER_GIVE_BACK_REMINDER1;
            UserType userType = UserType.USER_APP_AGENT;
            String title = Constants.AGENT_APP_NAME;
            PushPayload payload = new PushPayload(userType, sendType, title);
            payload.addUserId(orderInfo.getAgentUserId());
            payload.setContent(String.format(sendType.getMessage(), orderInfo.getAgentUserId()));
            sender.send(payload);
            return;
        }

        //订单到还车时间点，通知司机
        if (key.startsWith(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER_KEY)) {
            Long orderId = Long.valueOf(key.replaceFirst(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER_KEY, ""));
            log.info("[" + orderId + "]还车到点提醒");
            OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, null);
            if (orderInfo == null) return;
            SendType sendType = SendType.ORDER_GIVE_BACK_REMINDER;
            UserType userType = UserType.USER_APP_AGENT;
            String title = Constants.AGENT_APP_NAME;
            PushPayload payload = new PushPayload(userType, sendType, title);
            payload.addUserId(orderInfo.getAgentUserId());
            payload.setContent(String.format(sendType.getMessage(), orderInfo.getAgentUserId()));
            sender.send(payload);
        }
    }
}
