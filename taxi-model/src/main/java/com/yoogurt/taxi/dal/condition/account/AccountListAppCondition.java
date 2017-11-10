package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountListAppCondition extends PageableCondition {
    private Long userId;
    private Integer billType;
}
