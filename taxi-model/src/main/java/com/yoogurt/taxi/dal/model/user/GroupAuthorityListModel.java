package com.yoogurt.taxi.dal.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GroupAuthorityListModel {
    private String groupName;
    private List<AuthorityListModel> authorityList;
}
