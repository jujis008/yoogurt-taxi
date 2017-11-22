package com.yoogurt.taxi.dal.condition.user;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class DriverWebListCondition extends PageableCondition{
    private String name;
    private String username;
    private String idCard;
    private Integer userStatus;
    @Pattern(regexp = "20|30",message = "司机身份标识有误")
    private String userType;
}
