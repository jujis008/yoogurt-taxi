package com.yoogurt.taxi.dal.condition.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillListWebCondition extends AccountListWebCondition{
    private Integer billType;
}
