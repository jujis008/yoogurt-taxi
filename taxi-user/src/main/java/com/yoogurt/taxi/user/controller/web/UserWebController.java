package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * 后台用户管理接口
 * @Author Eric Lau
 * @Date 2017/9/4.
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login/username/{username}/password/{password}", method = RequestMethod.GET)
    public ResponseObj login(@PathVariable(name="username") String username, @PathVariable(name="password") String password) {

        return ResponseObj.success(userService.doLogin(username, password));
    }

    @RequestMapping("/info/{id}")
    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
        return ResponseObj.success(userService.getUserInfo(id));
    }

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }
}
