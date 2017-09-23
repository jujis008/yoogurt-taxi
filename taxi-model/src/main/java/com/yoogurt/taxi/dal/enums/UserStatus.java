package com.yoogurt.taxi.dal.enums;


public enum UserStatus {
    UN_ACTIVE(10,"未激活"),
    UN_AUTHENTICATE(20,"未认证"),
    AUTHENTICATED(30,"已认证"),
    FROZEN(40,"冻结"),
    ;
    private Integer code;
    private String name;

    public static UserStatus getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (UserStatus enums : UserStatus.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }

    UserStatus(Integer code, String name) {
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
