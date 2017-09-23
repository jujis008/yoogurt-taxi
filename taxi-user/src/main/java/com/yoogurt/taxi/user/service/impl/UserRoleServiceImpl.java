package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.user.dao.UserRoleDao;
import com.yoogurt.taxi.user.service.UserRoleService;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public ResponseObj saveUserRoleInfo(Long userId, List<Long> roleIdList) {
        for (Long roleId:roleIdList) {
            UserRoleInfo roleInfo = new UserRoleInfo();
            roleInfo.setRoleId(roleId);
            roleInfo.setUserId(userId);
            userRoleDao.insert(roleInfo);
        }
        return ResponseObj.success();
    }

    @Override
    public ResponseObj removeUserRole(Long id) {
        UserRoleInfo userRoleInfo = userRoleDao.selectById(id);
        if (userRoleInfo == null) {
            return ResponseObj.success();
        }
        userRoleInfo.setIsDeleted(Boolean.TRUE);
        userRoleDao.updateById(userRoleInfo);
        return ResponseObj.success();
    }
}
