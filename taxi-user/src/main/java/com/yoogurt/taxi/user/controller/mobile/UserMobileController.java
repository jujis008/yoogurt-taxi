package com.yoogurt.taxi.user.controller.mobile;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class UserMobileController {

    @Autowired
    private UserService userService;

    @RequestMapping("/info/{id}")
    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
        return ResponseObj.success(userService.getUserInfo(id));
    }

    @RequestMapping(value = "/info/username/{username}/password/{password}", method = RequestMethod.GET)
    public ResponseObj login(@PathVariable(name="username") String username, @PathVariable(name="password") String password) {

        return ResponseObj.success(userService.getUserInfo(username, password));
    }
}
