package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.common.condition.PeriodWithPageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficViolationListCondition extends PeriodWithPageableCondition {

    private Long orderId;

    private Long userId;

    private String plateNumber;

    private String vin;

    private Integer status;

    private boolean fromApp;

    private Integer userType;
}
