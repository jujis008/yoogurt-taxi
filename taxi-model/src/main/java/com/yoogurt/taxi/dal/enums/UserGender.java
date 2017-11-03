package com.yoogurt.taxi.dal.enums;

public enum UserGender {
    MALE(10,"男"),
    FEMALE(20,"女"),
    SECRET(30,"保密"),
    ;
    private Integer code;
    private String  name;

    UserGender(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static UserGender getEnumsByCode(Integer code) {
        for (UserGender gender:UserGender.values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        return null;
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
