package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.common.condition.PeriodWithPageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListCondition extends PeriodWithPageableCondition {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单号，精确匹配
     */
    private String orderId;

    /**
     * 司机姓名，精确匹配
     */
    private String driverName;

    /**
     * 司机手机号码，精确匹配
     */
    private String phone;

    /**
     * 订单状态。
     * @see com.yoogurt.taxi.dal.enums.OrderStatus
     */
    private Integer status;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 是否来自于App端的请求
     */
    private boolean fromApp;
}
