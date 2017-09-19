package com.yoogurt.taxi.dal.condition.user;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverWLCondition extends PageableCondition{
    private String name;
    private String username;
    private String idCard;
    private Integer userStatus;
    private Integer userType;
}
