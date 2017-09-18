package com.yoogurt.taxi.dal.enums;

public enum UserFromEnums {
    IMPORT(10,"导入"),
    APP(20,"app注册"),
    WEB(30,"web端注册")
    ;

    private Integer code;
    private String name;

    UserFromEnums(Integer code, String name) {
        this.code = code;
        this.name = name;
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
