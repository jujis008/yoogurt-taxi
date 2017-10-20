package com.yoogurt.taxi.system.controller.web;

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
@RequestMapping(value = "web/system/version")
public class AppVersionWebController {
    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = "info/type/{type}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getLatestVersion(@PathVariable(name = "type") Integer type) {
        AppType appType = AppType.getEnumsByCode(type);
        if (appType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"错误的应用类型");
        }
        AppVersion appVersion = appVersionService.getLatestOne(appType);
        if (appVersion == null) {
            return ResponseObj.fail(StatusCode.SYS_ERROR);
        }
        return ResponseObj.success(appVersion);
    }

}
