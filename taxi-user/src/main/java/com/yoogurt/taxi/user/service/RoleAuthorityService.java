package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.dal.model.AuthorityModel;

import java.util.List;

public interface RoleAuthorityService {

    List<AuthorityModel> getAuthoritiesByRoleId(Long roleId);
}
