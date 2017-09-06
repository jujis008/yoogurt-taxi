package com.yoogurt.taxi.user.controller.mobile;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.user.Form.LoginForm;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class UserMobileController {

    @Autowired
    private UserService userService;

    /**
     * 客户端请求登录，获取用户信息以及授权码，并缓存。
     * @param loginForm
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseObj login(@RequestBody LoginForm loginForm) {

        return ResponseObj.success(userService.doLogin(loginForm.getUsername(), loginForm.getPassword()));
    }

    @RequestMapping("/info/{id}")
    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
        return ResponseObj.success(userService.getUserInfo(id));
    }
}
