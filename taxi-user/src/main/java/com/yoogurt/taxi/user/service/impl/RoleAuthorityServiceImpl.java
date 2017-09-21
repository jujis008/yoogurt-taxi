package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
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
        for (Long authorityId:authorityIdList) {
            RoleAuthorityInfo roleAuthorityInfo = new RoleAuthorityInfo();
            roleAuthorityInfo.setAuthorityId(authorityId);
            roleAuthorityInfo.setRoleId(roleId);
            roleAuthorityDao.insert(roleAuthorityInfo);
        }
        return ResponseObj.success();
    }

    @Override
    public List<GroupAuthorityLModel> getAuthorityListByRoleId(Long roleId) {
        return roleAuthorityDao.getAuthorityListByRoleId(roleId);
    }

    @Override
    public ResponseObj removeRoleAuthority(Long id) {
        RoleAuthorityInfo roleAuthorityInfo = roleAuthorityDao.selectById(id);
        if (roleAuthorityInfo == null) {
            return ResponseObj.success();
        }
        roleAuthorityInfo.setIsDeleted(Boolean.TRUE);
        roleAuthorityDao.updateById(roleAuthorityInfo);
        return ResponseObj.success();
    }
}
