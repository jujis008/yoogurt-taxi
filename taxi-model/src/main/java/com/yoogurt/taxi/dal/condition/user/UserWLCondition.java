package com.yoogurt.taxi.dal.condition.user;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWLCondition extends PageableCondition {
    private String username;
    private String employeeNo;
    private String name;
    private Long roleId;
}
