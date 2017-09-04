package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.model.UserInfo;
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

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ResponseObj login(String username, String password) {

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("18814892833");
        userInfo.setPassword("123456");
        userInfo.setGmtCreate(new Date());
        userInfo.setName("Eric");
        return ResponseObj.success(userInfo);
    }
}
