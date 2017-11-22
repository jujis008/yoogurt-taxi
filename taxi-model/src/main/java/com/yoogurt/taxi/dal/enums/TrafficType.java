package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 违章类型：10-服务违章，20-交通违章
 */
@Getter
public enum TrafficType {
    /**
     *服务违章
     */
    SERVICE(10, "服务违章"),
    /**
     *交通违章
     */
    TRAFFIC(20, "交通违章"),
    ;

    private Integer code;

    private String name;

    TrafficType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public static TrafficType getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (TrafficType enums : TrafficType.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }
}
