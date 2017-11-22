package com.yoogurt.taxi.dal.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserWebListModel {
    private String userId;
    private String name;
    private String username;
    private String employeeNo;
    private String roleName;
    private Date gmtModify;
}
