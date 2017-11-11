package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.user.service.AuthorityInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthorityServiceTest {

    @Autowired
    private AuthorityInfoService authorityInfoService;

    @Test
    @Ignore
    public void allAuthorities() {
        List<GroupAuthorityLModel> allAuthorities = authorityInfoService.getAllAuthorities();
        System.out.println(ResponseObj.success(allAuthorities).toJSON());
    }

    @Test
    public void getAuthorityById() {
        AuthorityInfo authority = authorityInfoService.getAuthorityById(1L);
        System.out.println(ResponseObj.success(authority));
    }

    @Test
    public void getAuthorityWebList() {
        AuthorityWLCondition condition = new AuthorityWLCondition();
        condition.setAuthorityGroup("");
        condition.setAuthorityName("tt");
        condition.setUri("");
        Pager<AuthorityWLModel> authorityWebList = authorityInfoService.getAuthorityWebList(condition);
        System.out.println(ResponseObj.success(authorityWebList));
    }

    @Test
    public void removeAuthorityById() {
        authorityInfoService.removeAuthorityById(0L);
        System.out.println(ResponseObj.success());
    }

    @Test
    public void saveAuthorityInfo() {
        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setAssociatedControl("input_tag");
        authorityInfo.setAuthorityGroup("test1");
        authorityInfo.setAuthorityName("user_add");
        authorityInfo.setRemark("");
        authorityInfo.setUri("/web/user/addUser");
        authorityInfoService.saveAuthorityInfo(authorityInfo);
        System.out.println(ResponseObj.success().toJSON());
    }

    @Test
    public void test() {
        Long authorityId = 4L;
        ResponseObj responseObj = authorityInfoService.removeAuthorityById(authorityId);
        System.out.println(responseObj.toJSON());
    }
}
