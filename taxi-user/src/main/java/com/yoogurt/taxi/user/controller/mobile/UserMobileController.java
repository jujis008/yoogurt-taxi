package com.yoogurt.taxi.user.controller.mobile;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.service.LoginService;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class UserMobileController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginService    loginService;

//    /**
//     * 客户端请求登录，获取用户信息以及授权码，并缓存。
//     * @param loginForm
//     * @return
//     */
//    @RequestMapping(value = "/i/login", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
//    public ResponseObj login(@RequestBody LoginForm loginForm) {

//
//        SessionUser sessionUser = userService.doLogin(loginForm.getUsername(), loginForm.getPassword());
//        if (sessionUser != null) {
//            return ResponseObj.success(sessionUser);
//        }
//        return ResponseObj.fail(StatusCode.BIZ_FAILED.getStatus(), "登录失败，请核对用户名和密码");
//    }

//    @RequestMapping("/info/{id}")
//    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
//        return ResponseObj.success(userService.getUserInfo(id));
//    }

    @RequestMapping("/tt")
    public boolean tt() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(123576574L);
        return userService.addUserInfo(userInfo);
    }

    @RequestMapping(value = "/i/login", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj login(String username, String password) {
        if (StringUtils.isBlank(username)) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(),"请填写用户名");
        }
        if (StringUtils.isBlank(password)) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK.getStatus(),"请填写密码");
        }
        return loginService.login(username,password, UserType.USER_WEB);
    }
}
