package com.yoogurt.taxi.system.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;
import com.yoogurt.taxi.dal.enums.SysType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.system.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mobile/system")
public class AppVersionMobileController extends BaseController{
    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = "version/latest",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseObj getLatestVersion() {
        Integer userType = super.getUserType();
        AppType appType = null;
        if (userType.equals(UserType.USER_APP_AGENT)) {
            appType = AppType.agent;
        }
        if (userType.equals(UserType.USER_APP_OFFICE)) {
            appType = AppType.office;
        }
        SysType sysType = SysType.getEnumsByCode(super.getSysType());
        AppVersion latestOne = appVersionService.getLatestOne(appType, sysType);
        return ResponseObj.success(latestOne);
    }
}
