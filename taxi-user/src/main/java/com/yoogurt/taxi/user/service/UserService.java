package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.dal.beans.UserInfo;

public interface UserService {

    UserInfo getUserByUserId(Integer id);

    boolean addUserInfo(UserInfo userInfo);

    boolean deleteUserInfo(Long userId);
}
