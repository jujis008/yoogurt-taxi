package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.CommonUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.mq.NotificationSender;
import com.yoogurt.taxi.order.service.OrderBizService;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractOrderBizService implements OrderBizService {

    @Autowired
    private RestAccountService accountService;

    @Autowired
    private NotificationSender sender;

    public ResponseObj isAllowed(String userId, Integer userType) {

        RestResult<FinanceAccount> accountResult = accountService.getAccountByUserId(userId, userType);
        if (!accountResult.isSuccess()) {
            log.warn("[REST]{}", accountResult.getMessage());
            return ResponseObj.of(accountResult);
        }
        FinanceAccount account = accountResult.getBody();
        if (account.getReceivedDeposit() != null && account.getReceivedDeposit().doubleValue() >= account.getReceivableDeposit().doubleValue()) {
            return ResponseObj.success();
        }
        log.warn("[REST]{}", "押金未充足");
        Map<String, Object> extras = new HashMap<>(1);
        extras.put("redirect", "charge");
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "您的押金不足，请充值", extras);
    }

    /**
     * 触发消息推送
     *
     * @param orderInfo 订单信息
     * @param userType  推送对象的用户类型
     * @param sendType  推送类型
     * @param extras    额外的回传参数
     */
    public void push(OrderInfo orderInfo, UserType userType, SendType sendType, Map<String, Object> extras) {

        if (orderInfo == null || userType == null || sendType == null) {
            return;
        }
        String orderId = orderInfo.getOrderId();
        String message = sendType.getMessage();
        String title = userType.equals(UserType.USER_APP_AGENT) ? Constants.AGENT_APP_NAME : Constants.OFFICIAL_APP_NAME;
        String userId = userType.equals(UserType.USER_APP_AGENT) ? orderInfo.getAgentUserId() : orderInfo.getOfficialUserId();
        PushPayload payload = new PushPayload(userType, sendType, title);
        if (extras == null) {
            extras = new HashMap<>(0);
        }
        extras.put("orderId", orderId);
        payload.setExtras(extras);
        switch (sendType) {
            //已接单
            case ORDER_RENT:
                String type = userType.equals(UserType.USER_APP_AGENT) ? "求租" : "出租";
                String driverName = userType.equals(UserType.USER_APP_AGENT) ? CommonUtils.convertName(orderInfo.getAgentDriverName(), "师傅") : CommonUtils.convertName(orderInfo.getOfficialDriverName(), "师傅");
                payload.setContent(String.format(message, type, orderId, driverName));
                break;
            //已支付
            case ORDER_PAID:
                payload.setContent(String.format(message, orderId));
                break;
            //已交车
            case ORDER_HANDOVER:
            // 交车时间到，提醒车主
            case ORDER_HANDOVER_REMINDER:
            // 交车前1小时，提醒车主
            case ORDER_HANDOVER_REMINDER1:
            // 未在规定时间完成交车
            case ORDER_HANDOVER_UNFINISHED_REMINDER:
            //已还车，提醒车主
            case ORDER_GIVE_BACK:
            //还车时间到，提醒司机
            case ORDER_GIVE_BACK_REMINDER:
            //还车前1小时，提醒司机
            case ORDER_GIVE_BACK_REMINDER1:
            //订单结束，提醒司机
            case ORDER_FINISH:
            //订单无人接单，提醒发单者
            case ORDER_TIMEOUT:
            //订单被取消
            case ORDER_CANCEL:
            //车主录入违章记录，提醒司机
            case TRAFFIC_VIOLATION:
                payload.setContent(String.format(message, orderId));
                break;
            default:
                break;
        }
        payload.addUserId(userId);
        //推送请求进入消息队列
        sender.send(payload);
    }
}
