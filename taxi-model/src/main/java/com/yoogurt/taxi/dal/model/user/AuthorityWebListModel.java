package com.yoogurt.taxi.dal.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AuthorityWebListModel {
    private Long id;
    private String uri;
    private String authorityName;
    private String authorityGroup;
    private String remark;
    private Date gmtModify;
}
