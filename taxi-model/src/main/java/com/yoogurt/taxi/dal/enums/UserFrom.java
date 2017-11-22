package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum UserFrom {
    /**
     *导入
     */
    IMPORT(10,"导入"),
    /**
     *app注册
     */
    APP(20,"app注册"),
    /**
     * web端注册
     */
    WEB(30,"web端注册")
    ;

    private Integer code;
    private String name;

    UserFrom(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
