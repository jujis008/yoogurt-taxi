package com.yoogurt.taxi.dal.enums;

public enum TradeType {
    CHARGE(10,"充值"),
    WITHDRAW(20,"提现"),
    FINE_OUT(30,"罚款"),
    FINE_IN(40,"补偿"),
    INCOME(50,"收入"),
    ;

    private Integer code;
    private String name;

    TradeType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TradeType getEnumsBycode(Integer code) {
        for (TradeType enums:TradeType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }

    public boolean isAdd() {
        switch (this) {
            case CHARGE:
            case INCOME:
            case FINE_IN:
                return true;
            default:
                return false;
        }
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
