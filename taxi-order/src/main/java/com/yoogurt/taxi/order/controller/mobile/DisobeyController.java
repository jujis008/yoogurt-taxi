package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.order.service.DisobeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile/order")
public class DisobeyController extends BaseController {

    @Autowired
    private DisobeyService disobeyService;

    @RequestMapping(value = "/disobeys", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getDisobeyList(DisobeyListCondition condition) {
        if(!condition.validate()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        }

        condition.setUserId(super.getUserId());
        condition.setFromApp(true);
        return ResponseObj.success(disobeyService.getDisobeyList(condition));
    }

}
