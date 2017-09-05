package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.dal.model.UserInfo;

public interface UserService {

    UserInfo getUserInfo(Integer id);

    UserInfo getUserInfo(String username, String password);
}
