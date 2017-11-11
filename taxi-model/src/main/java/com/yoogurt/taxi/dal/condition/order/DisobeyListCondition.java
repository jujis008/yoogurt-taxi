package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.common.condition.PeriodWithPageableCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisobeyListCondition extends PeriodWithPageableCondition {

    private String orderId;

    private String userId;

    private String driverId;

    private Integer userType;

    private Integer disobeyType;

    private String driverName;

    private String mobile;

    private boolean fromApp;
}
