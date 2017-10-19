package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * <p class="detail">
 * 功能：透传消息发送类型，以便客户端接收到消息后做下一步操作。<br/>
 * <strong>
 * common-普通消息,
 * order-订单通知消息,
 * disobey-违约消息,
 * traffic-违章消息,
 * withdraw-提现消息，
 * verify-认证消息。
 * </strong>
 * </p>
 *
 * @author weihao.liu
 * @version V1.0
 */
@Getter
public enum SendType {

    COMMON(1000, "common", "您有一条未读消息，请点击查看"),
    ORDER_RENT(2010, "order_rent", "您的%s消息【%s】已由【%s】接单，点击查看详情。"),
    ORDER_PAID(2011, "order_paid", "订单【%s】已完成支付。"),
    ORDER_HANDOVER(2020, "order_handover", "您的交易【%s】车主已交车，点击查看详情。"),
    ORDER_HANDOVER_REMINDER(2021, "order_handover_reminder", "您的交易【%s】已到达交车时间，请完成交车操作，以免造成不必要的损失。"),
    ORDER_HANDOVER_REMINDER1(2022, "order_handover_reminder1", "您的交易【%s】距离交车时间还有1小时，请及时完成交车操作。"),
    ORDER_HANDOVER_UNFINISHED_REMINDER(2023, "order_handover_unfinished_reminder", "您的交易【%s】车主在规定时间内未交车操作，已自动取消，点击查看详情。"),
    ORDER_GIVE_BACK(2030, "order_give_back", "您的交易【%s】车辆已归还，点击查看详情。"),
    ORDER_GIVE_BACK_REMINDER(2031, "order_give_back_reminder", "您的交易【%s】已到达还车时间，请完成还车操作以免造成不必要的损失。"),
    ORDER_GIVE_BACK_REMINDER1(2032, "order_give_back_reminder1", "您的交易【%s】距离还车时间还有1小时，请及时完成还车操作。"),
    ORDER_FINISH(2040, "order_finish", "您的交易【%s】已完成交易，点击查看详情。"),
    ORDER_TIMEOUT(2050, "order_timeout", "您的%s消息【%s】超期无人接单，已自动取消，点击查看详情。"),
    ORDER_CANCEL(2060, "order_cancel", "您的交易【%s】被对方/客服取消，点击查看详情。"),
    ORDER_REFUND(2070, "order_refund", "您有一笔交易退款，点击查看详情。"),
    DISOBEY_FINE_IN(3010, "disobey_fine_in", "您收到一笔赔偿金，点击查看详情。"),
    DISOBEY_FINE_OUT(3020, "disobey_fine_out", "您受到一次违约罚款，点击查看详情。"),
    TRAFFIC_VIOLATION(4010, "traffic_violation", "您的交易【%s】收到一条违章投诉，请及时处理。"),
    WITHDRAW_SUCCESS(5010, "withdraw_success", "您的提现申请转账成功，点击查看详情。"),
    WITHDRAW_FAILED(5011, "withdraw_failed", "您的提现申请转账失败，点击查看详情。"),
    VERIFY_SUCCESS(6010, "verify_success", "恭喜您认证通过，若要发布租单，请先充值押金。"),
    VERIFY_FAILED(6000, "verify_failed", "您递交的资料未通过审核，请重新上传认证资料。"),;

    private int code;

    private String name;

    private String message;

    SendType(int code, String name, String message) {
        this.code = code;
        this.name = name;
        this.message = message;
    }

    public static SendType getEnumByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (SendType userType : SendType.values()) {
                if (type.equals(userType.getName())) {
                    return userType;
                }
            }
        }
        return null;
    }

    public static String getDetailByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (SendType userType : SendType.values()) {
                if (type.equals(userType.getName())) {
                    return userType.getMessage();
                }
            }
        }
        return null;
    }

}
