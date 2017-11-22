package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum AppType {
    /**
     *车主端
     */
    office(1,"车主端"),
    /**
     *司机端
     */
    agent(2,"司机端"),
    ;

    private Integer code;
    private String name;

    AppType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AppType getEnumsByCode(Integer code) {
        for (AppType enums:AppType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }
}

