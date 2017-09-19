package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;

import java.util.List;

public interface RoleInfoService {
    ResponseObj saveRoleInfo(RoleInfo roleInfo);
    ResponseObj removeRole(Long roleId);
    ResponseObj getRoleById(Long roleId);
    List<RoleWLModel>   getRoleWebList();
}
