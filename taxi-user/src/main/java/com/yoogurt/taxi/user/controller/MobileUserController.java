package com.yoogurt.taxi.user.controller;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile/user")
public class MobileUserController {


    @Autowired
    private UserService userService;

    @RequestMapping("/info/{id}")
    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
        ResponseObj obj = new ResponseObj();
        obj.setBody(userService.getUserInfo(id));
        return obj;
    }
}
