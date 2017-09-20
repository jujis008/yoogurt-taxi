package com.yoogurt.taxi.dal.condition.user;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityWLCondition extends PageableCondition{
    private String uri;
    private String authorityName;
    private String authorityGroup;
}
