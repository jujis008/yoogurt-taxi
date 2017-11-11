package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;

import java.util.List;

public interface UserRoleService {

    UserRoleInfo getUserRoleInfo(String userId, Long roleId);
    ResponseObj saveUserRoleInfo(String userId, List<Long> roleIdList);
    ResponseObj removeUserRole(Long id);
}
