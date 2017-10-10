package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 取消订单违约责任方：10-正式司机，20-代理司机，30-无责
 */
@Getter
public enum ResponsibleParty {

    OFFICIAL(10, "正式司机"),
    AGENT(20, "代理司机"),
    NONE(30, "无责"),
    ;

    private Integer code;
    private String name;

    ResponsibleParty(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
