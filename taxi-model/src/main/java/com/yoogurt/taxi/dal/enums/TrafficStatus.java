package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 违章处理状态：10-未处理，20-已处理，30-信息误报
 */
@Getter
public enum TrafficStatus {

    PENDING(10, "未处理"),
    NORMAL(20, "已处理"),
    WRONG(30, "信息误报"),
    ;

    private Integer code;
    private String name;

    TrafficStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public static TrafficStatus getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (TrafficStatus enums : TrafficStatus.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }
}
