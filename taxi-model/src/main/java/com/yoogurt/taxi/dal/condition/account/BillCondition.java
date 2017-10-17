package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.condition.PeriodCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillCondition extends PeriodCondition{
    private Long userId;
}
