package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.model.UserInfo;
import com.yoogurt.taxi.user.dao.UserDao;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserDao userDao;

    @Override
    public SessionUser doLogin(String username, String password) {

        UserInfo userInfo = getUserInfo(username);
        // 用户名不存在
        if(userInfo == null) return null;
        // 密码不匹配
        if (!Encipher.matches(password, userInfo.getPassword())) return null;
        //生成6位授权码
        String grantCode = RandomUtils.getRandNum(6);
        SessionUser sessionUser = new SessionUser(userInfo.getId().longValue(), username);
        sessionUser.setStatus(1);
        sessionUser.setGrantCode(grantCode);
        //缓存授权码，30秒内有效
        redisHelper.setObject(CacheKey.GRANT_CODE_KEY + userInfo.getId(), sessionUser, 300);
        return sessionUser;
    }

    @Override
    public UserInfo getUserInfo(Integer id) {

        return userDao.selectById(id);
    }

    @Override
    public UserInfo getUserInfo(String username) {
        UserInfo probe = new UserInfo();
        probe.setUsername(username);
        return userDao.selectOne(probe);
    }
}
