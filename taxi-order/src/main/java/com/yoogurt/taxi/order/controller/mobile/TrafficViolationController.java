package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.order.form.TrafficViolationForm;
import com.yoogurt.taxi.order.service.TrafficViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/mobile/order")
public class TrafficViolationController extends BaseController {

    @Autowired
    private TrafficViolationService trafficViolationService;


    @RequestMapping(value = "/trafficViolations", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getTrafficViolations(TrafficViolationListCondition condition) {
        if(!condition.validate()) return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        condition.setFromApp(true);
        condition.setUserId(super.getUserId());
        return ResponseObj.success(trafficViolationService.getTrafficViolations(condition));
    }

    @RequestMapping(value = "/trafficViolation", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj addTrafficViolation(@Valid @RequestBody TrafficViolationForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        form.setUserId(super.getUserId());
        ResponseObj obj = trafficViolationService.buildTrafficViolation(form);
        if(!obj.isSuccess()) return obj;
        OrderTrafficViolationInfo traffic = trafficViolationService.addTrafficViolation((OrderTrafficViolationInfo) obj.getBody());
        if(traffic != null) return ResponseObj.success(traffic);
        return ResponseObj.fail();
    }
}
