package com.yoogurt.taxi.gateway.service.impl;

import com.yoogurt.taxi.gateway.service.AuthService;
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

        return "OI6510990lk9897";
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

        return new User("18814892833", "$2a$10$OudvhfoZdFJlTqGCdxNqwuE.JOXgX7j9aLTsFXxspyxZRtoeJWdm6", true, true, true, true, new HashSet<>());
    }

}
