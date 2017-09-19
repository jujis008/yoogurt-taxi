package com.yoogurt.taxi.dal.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RoleWLModel {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private Date gmtCreate;
}
