package com.yoogurt.taxi.notification.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.notification.form.UserBindingForm;
import com.yoogurt.taxi.notification.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/mobile/notification")
public class PushMobileController extends BaseController {

    @Autowired
    private PushService pushService;

    @RequestMapping(value = "/binding", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj binding(@Valid @RequestBody UserBindingForm bindingForm, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        bindingForm.setUserId(super.getUserId());
        PushDevice device = pushService.binding(bindingForm);
        if (device != null) {
            return ResponseObj.success(device);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "设备绑定失败");
    }
}
