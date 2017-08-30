package com.yoogurt.taxi.auth.controller;

import com.yoogurt.taxi.auth.service.AuthService;
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

    @RequestMapping(value = "auth", method = RequestMethod.POST)
    public ResponseObj auth(String username, String password) {
        final String token = authService.auth(username, password);
        if (StringUtils.isNoneBlank(token)) {
            return ResponseObj.success(token);
        }
        return ResponseObj.fail();
    }
}
