package com.yoogurt.taxi.user.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
import com.yoogurt.taxi.dal.mapper.RoleAuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityListModel;

import java.util.List;

public interface RoleAuthorityDao extends IDao<RoleAuthorityInfoMapper, RoleAuthorityInfo> {

    List<AuthorityModel> getAuthoritiesByRoleId(Long roleId);

    List<GroupAuthorityListModel> getAuthorityListByRoleId(Long roleId);

    List<Long> getAuthorityIdListByRoleId(Long roleId);
}
