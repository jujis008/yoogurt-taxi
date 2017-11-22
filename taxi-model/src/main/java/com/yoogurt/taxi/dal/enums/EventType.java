package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum EventType {
    /**
     * 支付对象，支付成功时触发
     */
    PAY_SUCCEEDED("pay.succeeded", "支付成功"),
    /**
     * 退款对象，退款成功时触发
     */
    REFUND_SUCCEEDED("refund.succeeded", "退款成功"),
    /**
     * 企业支付对象，支付成功时触发
     */
    TRANSFER_SUCCEEDED("transfer.succeeded", "转账成功"),
    /**
     *批量企业付款对象，批量企业付款成功时触发
     */
    BATCH_TRANSFER_SUCCEEDED("batch_transfer.succeeded", "批量付款成功"),
    /**
     * 批量退款对象，批量退款成功时触发
     */
    BATCH_REFUND_SUCCEEDED("batch_refund.succeeded", "批量退款成功"),
    ;

    private String code;

    private String name;

    public static EventType getEnumsByCode(String code) {
        if(StringUtils.isNoneBlank(code)) {
            for (EventType enums : EventType.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }

    EventType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
