package com.yoogurt.taxi.gateway.service.impl;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.helper.TokenHelper;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import com.yoogurt.taxi.gateway.shiro.cache.AuthorizationCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthorizationCache authorizationCache;

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 获取一个新的token
     * @param userId 用户id
     * @param grantCode 登录接口返回的授权码
     * @param username 用户名
     * @param userType 用户类型
     * @return 新生成的token
     */
    @Override
    public String getAuthToken(Long userId, String grantCode, String username, Integer userType) {
        try {
            //生成token
            String authToken = tokenHelper.createToken(userId, username);

            //shiro认证
            UserAuthenticationToken token = new UserAuthenticationToken();
            token.setUserId(userId);
            token.setGrantCode(grantCode);
            token.setUsername(username);
            token.setToken(authToken);
            token.setUserType(userType);
            token.setRememberMe(true);
            token.setLoginAgain(false);

            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return authToken;
        } catch (Exception e) {
            log.error("授权失败, {}", e);
            return null;
        }
    }

    /**
     * 凭借原token，获取一个新的token
     * @param token 原来的token
     * @return 新的token
     */
    @Override
    public String refreshToken(String token) {
        if (StringUtils.isBlank(token)) return null;
        //先生成一个新的token
        String newToken = tokenHelper.refreshToken(token);
        if (StringUtils.isNoneBlank(newToken)) {
            Long userId = tokenHelper.getUserId(newToken);
            //获取缓存用户信息
            Object obj = redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);
            if(obj == null) return null; //缓存失效
            SessionUser user = (SessionUser) obj;
            //设置新的token
            user.setToken(newToken);
            //覆盖原来的用户信息
            redisHelper.setObject(CacheKey.SESSION_USER_KEY + userId, user);
            return newToken;
        }
        return null;
    }

    @Override
    public void clearCachedAuthorizationInfo(String... userIds) {
        if(userIds != null && userIds.length > 0){
            authorizationCache.remove(CacheKey.SHIRO_AUTHORITY_MAP, userIds);
        }
    }
}
