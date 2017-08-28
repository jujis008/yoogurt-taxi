package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.dal.model.UserInfo;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserInfo getUserInfo(Integer id) {

        return userDao.selectById(id);
    }
}
