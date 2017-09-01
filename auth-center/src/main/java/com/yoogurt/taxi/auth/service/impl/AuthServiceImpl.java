package com.yoogurt.taxi.auth.service.impl;

import com.yoogurt.taxi.auth.service.AuthService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return new User("18814892833", "123456", true, true, true, true, new HashSet<>());
    }

}
