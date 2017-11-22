package com.yoogurt.taxi.dal.enums;

public enum UserType {
    /**
     * 超级管理员
     */
    SUPER_ADMIN(0,"超级管理员"),
    /**
     * 后端用户
     */
    USER_WEB(10,"后端用户"),
    /**
     * 司机
     */
    USER_APP_AGENT(20,"司机"),
    /**
     * 车主
     */
    USER_APP_OFFICE(30,"车主"),
    ;

    private Integer code;

    private String name;

    public static UserType getEnumsByCode(Integer code) {
        if (code != null) {
            for (UserType enums: UserType.values()) {
                if(code.equals(enums.getCode())) {
                    return  enums;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否app用户
     * @return true 是
     */
    public boolean isAppUser () {
        switch (this) {
            case USER_APP_AGENT:
            case USER_APP_OFFICE:
                return true;
            default:
                return false;
        }
    }

    public Boolean isWebUser () {
        switch (this) {
            case SUPER_ADMIN:
            case USER_WEB:
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
