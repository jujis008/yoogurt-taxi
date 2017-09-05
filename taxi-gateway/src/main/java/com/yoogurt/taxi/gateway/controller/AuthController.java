package com.yoogurt.taxi.gateway.controller;

import com.auth0.jwt.JWTSigner;
import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

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
        JWTSigner signer = new JWTSigner("taxi123!@#");
        JWTSigner.Options options = new JWTSigner.Options();
        options.setExpirySeconds(180);
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("username", username);
        String sign = signer.sign(claims, options);
        return ResponseObj.success(sign);
    }
}
