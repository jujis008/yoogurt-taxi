package com.yoogurt.taxi.user.controller.rest;

import com.google.common.collect.Lists;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.dal.model.AuthorityModel;
import com.yoogurt.taxi.user.service.RoleAuthorityService;
import com.yoogurt.taxi.user.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/user")
public class RestUserController extends BaseController {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @RequestMapping(value = "/authorities/userId/{userId}", method = RequestMethod.GET)
    public ResponseObj authorities(@PathVariable(name = "userId") Long userId) {
        List<AuthorityModel> authorities = Lists.newArrayList();
        UserRoleInfo userRoleInfo = userRoleService.getUserRoleInfo(userId, null);
        if (userRoleInfo != null) {
            authorities = roleAuthorityService.getAuthoritiesByRoleId(userRoleInfo.getRoleId());
        }
        return ResponseObj.success(authorities);
    }
}
