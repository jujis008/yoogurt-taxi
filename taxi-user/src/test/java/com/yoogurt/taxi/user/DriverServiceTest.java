package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;
import com.yoogurt.taxi.dal.enums.UserGender;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.service.DriverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DriverServiceTest {
    @Autowired
    private DriverService driverService;

    @Test
    public void getDriverInfo() {
        String driverId = "1";
        DriverInfo driverInfo = driverService.getDriverInfo(driverId);
        System.out.println(ResponseObj.success(driverInfo).toJSON());
    }

    @Test
    public void getDriverWebList() {
        DriverWebListCondition condition = new DriverWebListCondition();
        condition.setIdCard("");
        condition.setName("");
        condition.setUsername("");
        Integer userStatus = UserStatus.AUTHENTICATED.getCode();
        condition.setUserStatus(userStatus);
        Integer userType = UserType.USER_APP_AGENT.getCode();
        condition.setUserType(String.valueOf(userType));
        ResponseObj driverWebList = driverService.getDriverWebList(condition);
        System.out.println(driverWebList);
    }

    @Test
    public void saveDriverInfo() {
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setAddress("");
        driverInfo.setDrivingLicense("");
        driverInfo.setDrivingLicenseBack("");
        driverInfo.setDrivingLicenseFront("");
        driverInfo.setGender(UserGender.FEMALE.getCode());
        driverInfo.setHousehold("");
        driverInfo.setIdBack("");
        driverInfo.setIdFront("");
        driverInfo.setIdCard("");
        driverInfo.setMobile("");
        driverInfo.setServiceNumber("");
        driverInfo.setServicePicture("");
        driverInfo.setType(UserType.USER_APP_AGENT.getCode());
        driverInfo.setUserId("0");
        driverService.saveDriverInfo(driverInfo);
        System.out.println(ResponseObj.success());
    }

    @Test
    public void removeDriver() {
        driverService.removeDriver("1");
        System.out.println(ResponseObj.success());
    }
}
