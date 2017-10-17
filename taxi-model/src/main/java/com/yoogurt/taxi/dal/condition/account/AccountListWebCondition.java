package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountListWebCondition extends PageableCondition {
    private String name;
    private String username;
    private Integer userType;
}
