package com.yoogurt.taxi.gateway.rest;

import com.yoogurt.taxi.dal.model.AuthorityModel;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "taxi-user")
public interface AuthorityService {

    @RequestMapping("/rest/user/authorities/userId/{userId}")
    List<AuthorityModel> getAuthoritiesByUserId(@PathVariable(name = "userId") Long userId);

}