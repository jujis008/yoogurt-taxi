package com.yoogurt.taxi.gateway.controller;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.enums.UserTypeEnums;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.common.helper.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * 认证接口
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
@RestController
@RequestMapping("/")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenHelper tokenHelper;

    @RequestMapping(value = "/token/userId/{userId}/grantCode/{grantCode}/username/{username}/type/{type}", method = RequestMethod.GET)
    public ResponseObj token(@PathVariable("userId") Long userId,
                             @PathVariable("grantCode") String grantCode,
                             @PathVariable("username") String username) {

        Integer userType = super.getUserType();
        String authToken = authService.getAuthToken(userId, grantCode, username, userType);
        if(StringUtils.isNoneBlank(authToken)) return ResponseObj.success(authToken);

        return ResponseObj.fail(StatusCode.BIZ_FAILED, "授权码不存在或已过期");
    }


    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseObj refresh(HttpServletRequest request) {
        String oldToken = tokenHelper.getAuthToken(request);
        String newToken = authService.refreshToken(oldToken);
        if (StringUtils.isNoneBlank(newToken)) {
            return ResponseObj.success(newToken);
        }
        return ResponseObj.fail(StatusCode.LOGIN_EXPIRE, StatusCode.LOGIN_EXPIRE.getDetail());
    }
}
