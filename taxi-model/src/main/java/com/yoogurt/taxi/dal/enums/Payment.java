package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 资金来源
 */
@Getter
public enum Payment {
    DEPOSIT(1,"押金"),
    BALANCE(2,"余额"),
    ALIPAY(3,"支付宝"),
    WEIXIN(4,"微信"),
    BANK(5,"银行"),
    OTHERS(6,"其它"),
    ;

    private Integer code;
    private String name;

    Payment(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Payment getEnumsBycode(Integer code) {
        for (Payment enums:Payment.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }

    public boolean isChargeType() {
        switch (this) {
            case ALIPAY:
            case WEIXIN:
            case BANK:
                return true;
            default:
                return false;
        }
    }

}
