package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.dal.beans.UserInfo;

public interface UserService {

    SessionUser doLogin(String username, String password);

    UserInfo getUserInfo(Integer id);

    UserInfo getUserInfo(String username);

    boolean addUserInfo(UserInfo userInfo);

    boolean deleteUserInfo(Long userId);
}
