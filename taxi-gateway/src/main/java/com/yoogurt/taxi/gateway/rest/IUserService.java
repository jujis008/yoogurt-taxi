package com.yoogurt.taxi.gateway.rest;

import com.yoogurt.taxi.dal.model.UserInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Description:
 *
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@FeignClient(value = "taxi-user")
public interface IUserService {

    @RequestMapping(value = "/mobile/user/info/username/{username}/password/{password}/", method = RequestMethod.GET)
    UserInfo getUserInfo(@PathVariable(name="username") String username, @PathVariable(name="password") String password);
}
