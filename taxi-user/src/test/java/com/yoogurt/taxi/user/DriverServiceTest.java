package com.yoogurt.taxi.user;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.enums.UserGender;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.service.DriverService;
import com.yoogurt.taxi.user.service.UserService;
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
        Long driverId = 1L;
        DriverInfo driverInfo = driverService.getDriverInfo(driverId);
        System.out.println(ResponseObj.success(driverInfo).toJSON());
    }

    @Test
    public void getDriverWebList() {
        DriverWLCondition condition = new DriverWLCondition();
        condition.setIdCard("");
        condition.setName("");
        condition.setUsername("");
        Integer userStatus = UserStatus.AUTHENTICATED.getCode();
        condition.setUserStatus(userStatus);
        Integer userType = UserType.USER_APP_AGENT.getCode();
        condition.setUserType(userType);
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
        driverInfo.setGender(UserGender.female.getCode());
        driverInfo.setHousehold("");
        driverInfo.setIdBack("");
        driverInfo.setIdFront("");
        driverInfo.setIdCard("");
        driverInfo.setMobile("");
        driverInfo.setServiceNumber("");
        driverInfo.setServicePicture("");
        driverInfo.setType(UserType.USER_APP_AGENT.getCode());
        driverInfo.setUserId(0L);
        driverService.saveDriverInfo(driverInfo);
        System.out.println(ResponseObj.success());
    }

    @Test
    public void removeDriver() {
        Long driverId = 1L;
        driverService.removeDriver(driverId);
        System.out.println(ResponseObj.success());
    }
}
