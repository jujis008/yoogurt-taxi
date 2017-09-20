package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;
import com.yoogurt.taxi.user.dao.RoleDao;
import com.yoogurt.taxi.user.service.RoleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleInfoServiceImpl implements RoleInfoService{
    @Autowired
    private RoleDao roleDao;
    @Override
    public ResponseObj saveRoleInfo(RoleInfo roleInfo) {
        if (roleInfo.getId() != null) {
            roleDao.updateById(roleInfo);
        }else {
            roleDao.insert(roleInfo);
        }
        return ResponseObj.success();
    }

    @Override
    public ResponseObj removeRole(Long roleId) {
        RoleInfo roleInfo = roleDao.selectById(roleId);
        if(roleInfo == null) {
            return ResponseObj.success();
        }
        roleInfo.setIsDeleted(Boolean.TRUE);
        roleDao.updateById(roleInfo);
        return ResponseObj.success();
    }

    @Override
    public ResponseObj getRoleById(Long roleId) {
        return ResponseObj.success(roleDao.selectById(roleId));
    }

    @Override
    public List<RoleWLModel> getRoleWebList() {
        return roleDao.getRoleWebList();
    }
}
