package com.yoogurt.taxi.gateway.controller;

import com.yoogurt.taxi.dal.model.UserInfo;
import com.yoogurt.taxi.gateway.jwt.JwtTokenUtil;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 分发ticket
     * @param request
     * @return
     */
    @RequestMapping(value = "auth", method = RequestMethod.GET)
    public ResponseObj auth(HttpServletRequest request) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setUsername("18814892833");
        userInfo.setPassword("123456");
        final String token = jwtTokenUtil.generateToken(userInfo);
        if (StringUtils.isNoneBlank(token)) {
            return ResponseObj.success(token);
        }
        return ResponseObj.fail();
    }
}
