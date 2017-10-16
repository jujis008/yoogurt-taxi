package com.yoogurt.taxi.dal.condition.notification;

import com.yoogurt.taxi.common.condition.PeriodWithPageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCondition extends PeriodWithPageableCondition {

    private Long userId;

    private Integer type;
}
