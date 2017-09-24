package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    HAND_OVER(10,"待交车"),
    PICK_UP(20,"待取车"),
    GIVE_BACK(30,"待还车"),
    ACCEPT(40,"待收车"),
    CANCELED(50,"手动取消"),
    ;
    private Integer code;

    private String name;

    public static OrderStatus getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (OrderStatus enums : OrderStatus.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }

    OrderStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
