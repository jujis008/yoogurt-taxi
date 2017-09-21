package com.yoogurt.taxi.dal.enums;

public enum UserGender {
    male(10,"男"),
    female(20,"女"),
    secret(30,"保密"),
    ;
    private Integer code;
    private String  name;

    UserGender(Integer code, String name) {
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
