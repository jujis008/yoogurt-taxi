package com.yoogurt.taxi.auth.service.impl;

import com.yoogurt.taxi.auth.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String auth(String clientId, String secret) {

        return null;
    }

    @Override
    public String refresh(String oldToken) {
        return null;
    }

    @Override
    public boolean validate(String token, String resource) {
        return false;
    }
}
