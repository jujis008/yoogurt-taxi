package com.yoogurt.taxi.dal.enums;

public enum UserType {
    SUPER_ADMIN(0,"超级管理员"),
    USER_WEB(10,"后端用户"),
    USER_APP_AGENT(20,"代理端用户"),
    USER_APP_OFFICE(30,"正式端用户"),
    ;

    private Integer code;
    private String name;

    public static UserType getEnumsByCode(Integer code) {
        for (UserType enums: UserType.values()) {
            if(code.equals(enums.getCode())) {
                return  enums;
            }
        }
        return null;
    }

    /**
     * 判断是否app用户
     * @param code
     * @return
     */
    public static Boolean isAppUser (Integer code) {
        switch (getEnumsByCode(code)) {
            case USER_APP_AGENT:
                return true;
            case USER_APP_OFFICE:
                return true;
            default:
                return false;
        }
    }

    UserType(Integer code, String name) {
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
