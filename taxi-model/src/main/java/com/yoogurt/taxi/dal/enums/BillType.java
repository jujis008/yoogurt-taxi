package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum BillType {
    DEPOSIT(10,"押金"),
    BALANCE(20,"余额"),
    ;

    private Integer code;
    private String name;

    BillType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BillType getEnumsByCode(Integer code) {
        for (BillType enums:BillType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }
}
