package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum SysType {
    android(1,"Android"),
    ios(2,"ios");

    private Integer code;
    private String name;

    SysType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SysType getEnumsByCode(Integer code) {
        for (SysType enums:SysType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }
}
