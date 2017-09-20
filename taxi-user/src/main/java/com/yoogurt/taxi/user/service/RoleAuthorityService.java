package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;

import java.util.List;

public interface RoleAuthorityService {

    List<AuthorityModel> getAuthoritiesByRoleId(Long roleId);
    ResponseObj saveRoleAuthorityInfo(Long roleId, List<Long> authorityIdList);
    List<GroupAuthorityLModel> getAuthorityListByRoleId(Long roleId);
    ResponseObj removeRoleAuthority(Long id);
}
