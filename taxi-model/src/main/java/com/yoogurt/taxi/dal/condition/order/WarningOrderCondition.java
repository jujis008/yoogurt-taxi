package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.dal.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 告警订单查询条件
 */
@Setter
@Getter
public class WarningOrderCondition {

    /**
     * 待取车
     */
    private Integer status = OrderStatus.PICK_UP.getCode();

    /**
     * 未支付
     */
    private Boolean isPaid = Boolean.FALSE;

    /**
     * 未删除
     */
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * 代理司机
     */
    private Long agentUserId;
}
