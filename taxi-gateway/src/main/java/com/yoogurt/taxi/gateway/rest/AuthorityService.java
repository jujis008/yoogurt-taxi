package com.yoogurt.taxi.gateway.rest;

<<<<<<< HEAD
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.model.AuthorityModel;
=======
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
>>>>>>> db1849fbabb28d3991a6520f7116a1eb0a4f1660
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "taxi-user")
public interface AuthorityService {

    @RequestMapping("/rest/user/authorities/userId/{userId}")
    RestResult<List<AuthorityModel>> getAuthoritiesByUserId(@PathVariable(name = "userId") Long userId);

}