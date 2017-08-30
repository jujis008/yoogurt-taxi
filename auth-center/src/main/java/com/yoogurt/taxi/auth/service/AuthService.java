package com.yoogurt.taxi.auth.service;


/**
 * 用户认证服务接口
 */
public interface AuthService {

    String auth(String clientId, String secret);

    String refresh(String oldToken);

    boolean validate(String token, String resource);
}
