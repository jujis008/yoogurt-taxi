package com.yoogurt.taxi.user.dao.impl;

import com.google.common.collect.Lists;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
import com.yoogurt.taxi.dal.mapper.RoleAuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.user.dao.RoleAuthorityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleAuthorityDaoImpl extends BaseDao<RoleAuthorityInfoMapper, RoleAuthorityInfo> implements RoleAuthorityDao {

    @Autowired
    private RoleAuthorityInfoMapper roleAuthorityInfoMapper;

    @Override
    public List<AuthorityModel> getAuthoritiesByRoleId(Long roleId) {
        if(roleId == null || roleId <= 0) return Lists.newArrayList();
        return roleAuthorityInfoMapper.getAuthoritiesByRoleId(roleId);
    }

    @Override
    public List<GroupAuthorityLModel> getAuthorityListByRoleId(Long roleId) {
        return roleAuthorityInfoMapper.getAuthorityListByRoleId(roleId);
    }

    @Override
    public List<Long> getAuthorityIdListByRoleId(Long roleId) {
        return roleAuthorityInfoMapper.getAuthorityIdListByRoleId(roleId);
    }
}
