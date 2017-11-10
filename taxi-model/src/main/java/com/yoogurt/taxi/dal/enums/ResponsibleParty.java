package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 取消订单违约责任方：10-车主，20-司机，30-无责
 */
@Getter
public enum ResponsibleParty {

    OFFICIAL(10, 30, "车主"),
    AGENT(20, 20, "司机"),
    NONE(30, 0, "无责"),
    ;

    private Integer code;

    private Integer userType;

    private String name;

    ResponsibleParty(Integer code, Integer userType, String name) {
        this.code = code;
        this.userType = userType;
        this.name = name;
    }

    public static ResponsibleParty getEnumsByType(Integer userType) {
        if(userType != null && userType > 0) {
            for (ResponsibleParty enums : ResponsibleParty.values()) {
                if (userType.equals(enums.getUserType())) {
                    return enums;
                }
            }
        }
        return null;
    }

    public static ResponsibleParty getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (ResponsibleParty enums : ResponsibleParty.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }

}
