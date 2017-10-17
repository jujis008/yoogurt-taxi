package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * <p class="detail">
 * 功能：透传消息发送类型，以便客服端接受到消息后做下一步操作<br/>
 * <strong>
 *   common-普通消息,
 *   verify-认证消息,
 *   order-订单通知消息,
 *   disobey-违约消息,
 *   traffic-违章消息,
 *   withdraw-提现消息。
 * </strong>
 * </p>
 *
 * @author weihao.liu
 * @version V1.0
 */
@Getter
public enum SendType {

    COMMON(1000, "common", "您有一条未读消息，请点击查看"),
    VERIFY_SUCCESS(2010, "verify_success", "【司机认证通过】恭喜您认证通过，若要发布租单，请先充值押金。"),
    VERIFY_FAILED(2000, "verify_failed", "【司机未认证通过】您递交的资料未通过审核，请重新上传认证资料。"),
    ORDER_PAID(3020, "order_paid", "【出货单已支付】您有一笔出货单已支付，请尽快发货。"),
    ORDER_DELIVERED(3030, "order_delivered", "【采购单已发货】您有一笔采购单已发货，请尽快确认收货。"),
    ORDER_RECEIVED(3040, "order_received", "【出货单已收货】您有一笔出货单已确认收货，请点击查看。"),
    ORDER_TIMEOUT(4040, "order_timeout", "【采购单已超时】您有一笔采购单已超时，系统将自动取消该笔订单。"),
    ORDER_REFUND_SUBMIT(4010, "order_refund_submit", "【退款申请卖家同意退款】您有一笔采购单已申请退款成功，我们将会尽快处理、请耐心等待。"),
    ORDER_REFUND_APPLIED(4020, "order_refund_applied", "【退款申请平台审核通过】您有一笔退款单已申请退款成功，支付金额将在1-7个工作日内原路退还至您的账户，请注意查收。"),
    ORDER_REFUND_APPLIED_OFF(4050, "order_refund_applied_off", "【退款申请卖家拒绝退款】您有一笔采购单已申请退款失败,请注意查看."),
    ORDER_REFUND_DELIVERED(4060, "order_refund_delivered", "【退货单已发货】您有一笔退货单已发货，请尽快确认收货。"),
    ORDER_REFUND(4030, "order_refund", "【出货单退款成功】您有一笔出货单已退款，请注意查收。"),
    SHOP_OPENED(5010, "shop_opened", "【店铺已开通】恭喜您，店铺已成功开通，请到您的店铺主页查看权限。");

    private int code;

    private String type;

    private String message;

    SendType(int code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public static SendType getEnumByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (SendType userType : SendType.values()) {
                if (type.equals(userType.getType())) {
                    return userType;
                }
            }
        }
        return null;
    }

    public static String getDetailByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (SendType userType : SendType.values()) {
                if (type.equals(userType.getType())) {
                    return userType.getMessage();
                }
            }
        }
        return null;
    }

}
