package com.yoogurt.taxi.dal.enums;

public enum UserTypeEnums {
    SUPER_ADMIN(0,"超级管理员"),
    USER_WEB(10,"后端用户"),
    USER_APP_AGENT(20,"代理端用户"),
    USER_APP_OFFICE(30,"正式端用户"),
    ;

    private Integer code;
    private String name;

    public static UserTypeEnums getEnumsByCode(Integer code) {
        for (UserTypeEnums enums:UserTypeEnums.values()) {
            if(code.equals(enums.getCode())) {
                return  enums;
            }
        }
        return null;
    }

    UserTypeEnums(Integer code, String name) {
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
