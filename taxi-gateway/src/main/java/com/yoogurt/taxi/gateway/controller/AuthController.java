package com.yoogurt.taxi.gateway.controller;

import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    /**
     * 分发ticket
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.GET)
    public ResponseObj auth(String username, String password) {
        final String token = authService.auth(username, password);
        if (StringUtils.isNoneBlank(token)) {
            return ResponseObj.success(token);
        }
        return ResponseObj.fail();
    }
}
