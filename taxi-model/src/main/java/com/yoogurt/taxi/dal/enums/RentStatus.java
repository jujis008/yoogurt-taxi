package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

/**
 * 10-待接单，20-已接单，但订单还未结束，30-手动取消，40-超时自动取消
 */
@Getter
public enum RentStatus {
    /**
     *待接单
     */
    WAITING(10,"待接单"),
    /**
     *已接单
     */
    RENT(20,"已接单"),
    /**
     *手动取消
     */
    CANCELED(30,"手动取消"),
    /**
     *超时自动取消
     */
    TIMEOUT(40,"超时自动取消"),
    /**
     *已完成
     */
    FINISH(50,"已完成"),
    ;
    private Integer code;
    private String name;

    public static RentStatus getEnumsByCode(Integer code) {
        if(code != null && code > 0) {
            for (RentStatus enums : RentStatus.values()) {
                if (code.equals(enums.getCode())) {
                    return enums;
                }
            }
        }
        return null;
    }

    RentStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean isAllowOrder() {
        switch (this) {
            case WAITING:
                return true;
            default:
                return false;
        }
    }
}

