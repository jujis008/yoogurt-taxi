package com.yoogurt.taxi.common.controller;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.helper.ServletHelper;
import com.yoogurt.taxi.common.helper.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * 公共Controller，所有Controller继承于此
 * @Author Eric Lau
 * @Date 2017/8/28.
 */
public class BaseController {

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 从请求中获取UserID
     * @return userId
     */
    public Long getUserId() {
        return tokenHelper.getUserId(ServletHelper.getRequest());
    }

    /**
     * 从请求中获取userName
     * @return username
     */
    public String getUserName() {
        return tokenHelper.getUserName(ServletHelper.getRequest());
    }

    /**
     * 从请求中获取用户类型
     * @return 用户类型
     */
    public Integer getUserType() {
        HttpServletRequest request = ServletHelper.getRequest();
        return tokenHelper.getUserType(request);
    }

    /**
     * 从缓存中获取{@link SessionUser} SessionUser
     * @return SessionUser
     */
    public SessionUser getUser() {
        Long userId = getUserId();
        if(userId == null) return null;
        Object obj = redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);
        return obj != null ? (SessionUser) obj: null;
    }
}
