package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum AccountChangeType {
    frozen_add(10,"冻结"),
    frozen_deduct(20,"冻结扣除"),
    frozen_back(30,"冻结返还"),
    ;

    private Integer code;
    private String name;

    AccountChangeType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AccountChangeType getEnumsByCode(Integer code) {
        for (AccountChangeType enums:AccountChangeType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }
}
