package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.service.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisHelper redisHelper;

    @Test
    @Ignore
    public void getUserWebList() {
        UserWLCondition condition = new UserWLCondition();
        condition.setEmployeeNo("9901");
        condition.setName("");
        condition.setRoleId(null);
        condition.setUsername("");
        Pager<UserWLModel> userWebList = userService.getUserWebList(condition);
        Date gmtModify = userWebList.getDataList().get(0).getGmtModify();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateTime = formatter.format(gmtModify);
        System.out.println(dateTime);
        System.out.println(ResponseObj.success(userWebList).toJSON());
    }

    @Test
    @Ignore
    public void getUserByUserId() {
        UserInfo user = userService.getUserByUserId("666");
        System.out.println(ResponseObj.success(user).toJSON());
    }

    @Test
    @Ignore
    public void modifyHeadPicture() {
        userService.modifyHeadPicture("666", "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1484342823,356366591&fm=27&gp=0.jpg");
    }

    @Test
    @Ignore
    public void modifyLoginPassword() {
        ResponseObj obj = userService.modifyLoginPassword("666", "e10adc3949ba59abbe56e057f20f883e", "c33367701511b4f6020ec61ded352059");
        System.out.println(obj.toJSON());
    }

    @Test
    public void modifyPayPwd() {
        ResponseObj responseObj = userService.modifyPayPwd("666", "e10adc3949ba59abbe56e057f20f883e", "c33367701511b4f6020ec61ded352059");
        System.out.println(responseObj.toJSON());
    }

    @Test
    @Ignore
    public void modifyUserName() {
        String userid = "666";
        String password = "c33367701511b4f6020ec61ded352059";
        String phoneCode = RandomUtils.getRandNum(6);
        String phoneNumber = "17364517746";
        redisHelper.set(CacheKey.VERIFY_CODE_KEY+phoneNumber,phoneCode,180);
        ResponseObj responseObj = userService.modifyUserName(userid, password, phoneCode, phoneNumber);
        System.out.println(responseObj.toJSON());
    }

}
