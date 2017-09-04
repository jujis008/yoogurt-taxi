package com.yoogurt.taxi.gateway.service;


import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户认证服务接口
 */
public interface AuthService extends UserDetailsService {

    String auth(String clientId, String secret);

    String refresh(String oldToken);

    boolean validate(String token, String resource);
}
