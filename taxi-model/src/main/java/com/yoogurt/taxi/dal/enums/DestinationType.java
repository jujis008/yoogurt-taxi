package com.yoogurt.taxi.dal.enums;

public enum DestinationType {
    /**
     * 押金
     */
    DEPOSIT(1,"押金"),
    /**
     * 余额
     */
    BALANCE(2,"余额"),
    /**
     * 支付宝
     */
    ALIPAY(3,"支付宝"),
    /**
     * 微信
     */
    WEIXIN(4,"微信"),
    /**
     * 银行
     */
    BANK(5,"银行"),
    /**
     * 平台
     */
    PLATFORM(7,"平台"),
    /**
     * 其它
     */
    OTHERS(6,"其它"),
    ;

    private Integer code;
    private String name;

    DestinationType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DestinationType getEnumsBycode(Integer code) {
        for (DestinationType enums:DestinationType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
