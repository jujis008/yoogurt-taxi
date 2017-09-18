package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserFrom;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

//    @RequestMapping(value = "/login/username/{username}/password/{password}", method = RequestMethod.GET)
//    public ResponseObj login(@PathVariable(name="username") String username, @PathVariable(name="password") String password) {
//
//        return ResponseObj.success(userService.doLogin(username, password));
//    }

//    @RequestMapping("/info/{id}")
//    public ResponseObj userInfo(@PathVariable(name = "id") Integer id) {
//        return ResponseObj.success(userService.getUserInfo(id));
//    }

    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public ResponseObj saveUser(String username, String password, String name) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setLoginPassword(password);
        user.setName(name);
        user.setStatus(UserStatus.AUTHENTICATED.getCode());
        user.setType(UserType.USER_WEB.getCode());
        user.setUserFrom(UserFrom.WEB.getCode());
        user.setUserId(new Long("1232131463"));
        user.setIsDeleted(Boolean.TRUE);
        user.setGmtCreate(new Date());
        user.setCreator(8888L);
        user.setGmtModify(new Date());
        user.setModifier(8888L);
        return ResponseObj.success(userService.addUserInfo(user));
    }

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }
}
