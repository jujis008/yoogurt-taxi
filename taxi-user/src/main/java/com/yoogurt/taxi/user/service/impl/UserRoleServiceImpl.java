package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.user.dao.UserRoleDao;
import com.yoogurt.taxi.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public UserRoleInfo getUserRoleInfo(Long userId, Long roleId) {

        if ((userId == null || userId <= 0) && (roleId == null || roleId <= 0)) return null;
        UserRoleInfo info = new UserRoleInfo();

        if (userId != null) {
            info.setUserId(userId);
        }
        if (roleId != null) {
            info.setRoleId(roleId);
        }
        return userRoleDao.selectOne(info);
    }
}
