package com.yoogurt.taxi.dal.enums;


public enum UserStatusEnums {
    UN_ACTIVE(10,"未激活"),
    UN_AUTHENTICATE(20,"未认证"),
    AUTHENTICATED(30,"已认证"),
    FROZEN(40,"冻结"),
    ;
    private Integer code;
    private String name;

    public static UserStatusEnums getEnumsByCode(Integer code) {
        for (UserStatusEnums enums:UserStatusEnums.values()) {
            if(code.equals(enums)) {
                return enums;
            }
        }
        return null;
    }

    UserStatusEnums(Integer code, String name) {
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
