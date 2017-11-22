package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.dal.enums.TrafficStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.form.TrafficHandleForm;
import com.yoogurt.taxi.order.form.TrafficViolationForm;
import com.yoogurt.taxi.order.service.TrafficViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/mobile/order")
public class TrafficViolationController extends BaseController {

    @Autowired
    private TrafficViolationService trafficViolationService;

    @RequestMapping(value = "/trafficViolations", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getTrafficViolations(TrafficViolationListCondition condition) {
        if (!condition.validate()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        }
        condition.setFromApp(true);
        condition.setUserId(super.getUserId());
        condition.setUserType(super.getUserType());
        return ResponseObj.success(trafficViolationService.getTrafficViolations(condition));
    }

    @RequestMapping(value = "/trafficViolations/{id}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getTrafficViolation(@PathVariable(name = "id") Long id) {
        OrderTrafficViolationInfo traffic = trafficViolationService.getTrafficViolationInfo(id);
        return ResponseObj.success(traffic);
    }

    /**
     * 录入违章信息
     *
     * @param form   form表单
     * @param result 表单验证结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/trafficViolation", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj addTrafficViolation(@Valid @RequestBody TrafficViolationForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        SessionUser user = super.getUser();
        if (!UserType.USER_APP_OFFICE.getCode().equals(user.getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "您目前无法录入违章记录");
        }
        form.setUserId(user.getUserId());
        ResponseObj obj = trafficViolationService.buildTrafficViolation(form);
        if (!obj.isSuccess()) {
            return obj;
        }
        OrderTrafficViolationInfo traffic = trafficViolationService.addTrafficViolation((OrderTrafficViolationInfo) obj.getBody());
        if (traffic != null) {
            return ResponseObj.success(traffic);
        }
        return ResponseObj.fail();
    }

    /**
     * 处理违章记录
     *
     * @param disobeyForm form表单
     * @param result      表单验证结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/trafficViolation", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj trafficHandle(@Valid @RequestBody TrafficHandleForm disobeyForm, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (!UserType.USER_APP_OFFICE.getCode().equals(super.getUser().getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "您目前无法处理此违章");
        }
        OrderTrafficViolationInfo info = trafficViolationService.modifyStatus(disobeyForm.getId(), TrafficStatus.NORMAL);
        return info != null ? ResponseObj.success(info) : ResponseObj.fail();
    }
}
