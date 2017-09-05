package com.yoogurt.taxi.gateway.controller;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TokenHelper tokenHelper;

    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    public ResponseObj auth(String username, String password) {
        try {
            UserAuthenticationToken token = new UserAuthenticationToken(username, password, "TAXI_MOBILE");
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
        } catch (Exception e) {
            log.error("登录失败, {}", e);
            return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "登录失败，请核对账号和密码");
        }
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("username", username);
        return ResponseObj.success(tokenHelper.createToken(claims));
    }
}
