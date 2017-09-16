package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.bo.SessionUser;

public interface UserService {

    SessionUser doLogin(String username, String password);

    UserInfo getUserInfo(Integer id);

    UserInfo getUserInfo(String username);
}
