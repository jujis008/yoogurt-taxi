package com.yoogurt.taxi.gateway.service;

public interface AuthService {

    String getAuthToken(Long userId, String grantCode, String username, Integer userType);

    String refreshToken(String token);

    void clearCachedAuthorizationInfo();
}
