package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 违约类型：10-正式司机交车超时，20-代理司机还车超时，30-司机取消(无责取消不记录违约)
 */
@Getter
public enum DisobeyType {

    OFFICIAL_DRIVER_HANDOVER_TIMEOUT(10, "正式司机交车超时"),
    AGENT_DRIVER_HANDOVER_TIMEOUT(20, "代理司机还车超时"),
    CANCEL_ORDER(30, "司机取消"),
    ;

    private Integer code;
    private String name;


    DisobeyType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
