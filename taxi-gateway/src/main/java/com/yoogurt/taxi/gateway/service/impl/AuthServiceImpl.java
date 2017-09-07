package com.yoogurt.taxi.gateway.service.impl;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public String getAuthToken(String userId, String grantCode, String username) {
        String authToken = "";
        try {
            UserAuthenticationToken token = new UserAuthenticationToken();
            token.setUserId(userId);
            token.setGrantCode(grantCode);
            token.setUsername(username);
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            Map<String, Object> claims = Maps.newHashMap();
            claims.put("userId", userId);
            claims.put("username", username);
            authToken = tokenHelper.createToken(claims);
            redisHelper.set(CacheKey.TOKEN_KEY + userId, authToken);
        } catch (Exception e) {
            log.error("授权失败, {}", e);
        }
        return authToken;
    }
}
