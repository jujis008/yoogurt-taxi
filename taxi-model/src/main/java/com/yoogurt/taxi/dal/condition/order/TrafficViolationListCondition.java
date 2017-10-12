package com.yoogurt.taxi.dal.condition.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficViolationListCondition {

    private Long orderId;

    private Long userId;

    private String plateNumber;

    private String vin;

    private Integer status;
}
