package com.yoogurt.taxi.dal.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    /**
     *待交车
     */
    HAND_OVER(10,"待交车"),
    /**
     *待取车
     */
    PICK_UP(20,"待取车"),
    /**
     *待还车
     */
    GIVE_BACK(30,"待还车"),
    /**
     *待收车
     */
    ACCEPT(40,"待收车"),
    /**
     *已完成
     */
    FINISH(50,"已完成"),
    /**
     *已取消
     */
    CANCELED(60,"已取消"),
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

    /**
     * 获取下一个订单状态
     * @return 下一个订单状态
     */
    public OrderStatus next() {
        switch (this) {
            case HAND_OVER:
                return OrderStatus.PICK_UP;
            case PICK_UP:
                return OrderStatus.GIVE_BACK;
            case GIVE_BACK:
                return OrderStatus.ACCEPT;
            case ACCEPT:
                return OrderStatus.FINISH;
            default:
                return null;
        }
    }

    /**
     * 获取上一个订单状态
     * @return 上一个订单状态
     */
    public OrderStatus previous() {
        switch (this) {
            case PICK_UP:
                return OrderStatus.HAND_OVER;
            case GIVE_BACK:
                return OrderStatus.PICK_UP;
            case ACCEPT:
                return OrderStatus.GIVE_BACK;
            case FINISH:
                return OrderStatus.ACCEPT;
            default:
                return null;
        }
    }

    OrderStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
