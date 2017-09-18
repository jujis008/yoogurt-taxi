package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.enums.UserTypeEnums;

public interface LoginService {

    ResponseObj login(String username, String password, UserTypeEnums userType);

    ResponseObj register(String username, String password, UserTypeEnums userType);
}
