package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserAddress;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.service.UserAddressService;
import com.yoogurt.taxi.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AddressServiceTest {
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private UserService userService;

    @Test
    public void saveUserAddress() {
        UserAddress userAddress = new UserAddress();
        userAddress.setAddress("");
        userAddress.setIsPrimary(Boolean.TRUE);
        userAddress.setLat(36.21235677458841);
        userAddress.setLng(12.548354862588);
        userAddress.setUserId(1L);
        userAddressService.saveUserAddress(userAddress);
        System.out.println(ResponseObj.success());
    }

    @Test
    public void getUserAddressById() {
        UserAddress userAddress = userAddressService.getUserAddressById(2L);
        System.out.println(ResponseObj.success(userAddress));
    }

    @Test
    public void getUserList() {
        UserWLCondition condition = new UserWLCondition();
        Pager<UserWLModel> userWebList = userService.getUserWebList(condition);
        log.error(ResponseObj.success(userWebList).toJSON());
    }
}
