package com.yoogurt.taxi.system.controller.mobile;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;
import com.yoogurt.taxi.system.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mobile/system/version")
public class AppVersionMobileController {
    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = "/latest/type/{type}",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public ResponseObj getLatestVersion(@PathVariable(name = "type") Integer type) {
        AppType appType = AppType.getEnumsByCode(type);
        if (appType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"参数值错误");
        }
        AppVersion latestOne = appVersionService.getLatestOne(appType);
        if (latestOne == null) {
            return ResponseObj.fail(StatusCode.SYS_ERROR);
        }
        return ResponseObj.success(latestOne);
    }
}
