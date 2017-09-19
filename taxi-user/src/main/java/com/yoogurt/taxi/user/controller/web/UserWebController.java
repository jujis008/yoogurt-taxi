package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }
}
