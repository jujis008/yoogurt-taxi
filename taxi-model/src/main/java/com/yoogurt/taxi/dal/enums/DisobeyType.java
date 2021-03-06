package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 违约类型：10-车主交车超时，20-司机还车超时，30-司机取消(无责取消不记录违约)
 */
@Getter
public enum DisobeyType {
    /**
     *车主交车超时
     */
    OFFICIAL_DRIVER_HANDOVER_TIMEOUT(10, "车主交车超时"),
    /**
     *司机还车超时
     */
    AGENT_DRIVER_HANDOVER_TIMEOUT(20, "司机还车超时"),
    /**
     *司机取消
     */
    CANCEL_ORDER(30, "司机取消"),
    ;

    private Integer code;
    private String name;


    DisobeyType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
