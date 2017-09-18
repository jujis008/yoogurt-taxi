package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.dal.beans.UserRoleInfo;

public interface UserRoleService {

    UserRoleInfo getUserRoleInfo(Long userId, Long roleId);
}
