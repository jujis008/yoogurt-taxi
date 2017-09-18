package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;


    @Override
    public UserInfo getUserByUserId(Integer id) {

        return userDao.selectById(id);
    }


    @Override
    public boolean addUserInfo(UserInfo userInfo) {
        return userDao.insert(userInfo) == 1;
    }

    @Override
    public boolean deleteUserInfo(Long userId) {
        return userDao.deleteById(userId) == 1;
    }
}
