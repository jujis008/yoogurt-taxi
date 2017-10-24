package com.yoogurt.taxi.system.controller.web;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.BeanUtilsExtends;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;
import com.yoogurt.taxi.dal.enums.SysType;
import com.yoogurt.taxi.system.controller.form.AppVersionForm;
import com.yoogurt.taxi.system.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "web/system/version")
public class AppVersionWebController extends BaseController{
    @Autowired
    private AppVersionService appVersionService;

    @RequestMapping(value = "list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj list() {
        return ResponseObj.success(appVersionService.getList());
    }

    @RequestMapping(value = "save",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj save(@RequestBody @Valid AppVersionForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        SysType sysType = SysType.getEnumsByCode(form.getSysType());
        if (sysType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"系统类型不正确");
        }
        AppType appType = AppType.getEnumsByCode(form.getAppType());
        if (appType == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"应用类型不正确");
        }
        AppVersion appVersion = new AppVersion();
        BeanUtilsExtends.copyProperties(appVersion,form);
        return ResponseObj.success(appVersionService.save(appVersion));
    }
}
