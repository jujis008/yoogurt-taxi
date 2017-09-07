package com.yoogurt.taxi.gateway.controller;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Description:
 *
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
@RestController
@RequestMapping("/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/token/userId/{userId}/grantCode/{grantCode}/username/{username}", method = RequestMethod.GET)
    public ResponseObj token(@PathVariable("userId") String userId, @PathVariable("grantCode") String grantCode, @PathVariable("username") String username) {

        String authToken = authService.getAuthToken(userId, grantCode, username);
        if(StringUtils.isNoneBlank(authToken)){
            return ResponseObj.success(authToken);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "授权码不存在或已过期");
    }
}
