package com.yoogurt.taxi.gateway.controller;

import com.yoogurt.taxi.common.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-02 13:43
 */
@Slf4j
@Controller
public class SecurityController extends BaseController {

    /**
     * 没有ticket的访问都被重定向到登录页面
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

}
