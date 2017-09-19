package com.yoogurt.taxi.user.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
import com.yoogurt.taxi.dal.mapper.RoleAuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.AuthorityModel;

import java.util.List;

public interface RoleAuthorityDao extends IDao<RoleAuthorityInfoMapper, RoleAuthorityInfo> {

    List<AuthorityModel> getAuthoritiesByRoleId(Long roleId);
}