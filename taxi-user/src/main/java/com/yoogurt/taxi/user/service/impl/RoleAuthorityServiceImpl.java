package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.user.dao.RoleAuthorityDao;
import com.yoogurt.taxi.user.service.RoleAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleAuthorityServiceImpl implements RoleAuthorityService {

    @Autowired
    private RoleAuthorityDao roleAuthorityDao;

    @Override
    public List<AuthorityModel> getAuthoritiesByRoleId(Long roleId) {

        return roleAuthorityDao.getAuthoritiesByRoleId(roleId);
    }

    @Override
    public ResponseObj saveRoleAuthorityInfo(Long roleId, List<Long> authorityIdList) {
        return null;
    }

    @Override
    public List<GroupAuthorityLModel> getAuthorityListByRoleId(Long roleId) {
        return null;
    }

    @Override
    public ResponseObj removeRoleAuthority(Long id) {
        return null;
    }
}
