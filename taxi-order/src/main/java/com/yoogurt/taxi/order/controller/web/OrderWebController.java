package com.yoogurt.taxi.order.controller.web;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.condition.order.*;
import com.yoogurt.taxi.dal.enums.ResponsibleParty;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("web/order")
public class OrderWebController extends BaseController{
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private RentInfoService rentInfoService;
    @Autowired
    private TrafficViolationService trafficViolationService;
    @Autowired
    private CancelService cancelService;
    @Autowired
    private DisobeyService disobeyService;
    @Autowired
    private AcceptService   acceptService;
    @Autowired
    private PickUpService   pickUpService;

    @RequestMapping(value = "list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getList(OrderListCondition condition) {
        if (!UserType.getEnumsByCode(super.getUser().getType()).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        return ResponseObj.success(orderInfoService.getWebOrderList(condition));
    }

    @RequestMapping(value = "info/orderId/{orderId}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getDetail(@PathVariable(name = "orderId") Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, null);
        OrderAcceptInfo acceptInfo = acceptService.getAcceptInfo(orderId);
        OrderPickUpInfo pickUpInfo = pickUpService.getPickUpInfo(orderId);
        List<OrderDisobeyInfo> disobeyList = disobeyService.getDisobeyList(orderId, null, null);
        Map<String,Object> map = new HashMap<>();
        map.put("orderInfo",orderInfo);
        if (acceptInfo != null) {
            map.put("acceptInfo",acceptInfo);
        }
        if (pickUpInfo != null) {
            map.put("pickUpInfo",pickUpInfo);
        }
        if (CollectionUtils.isNotEmpty(disobeyList)) {
            map.put("disobeyList",disobeyList);
        }
        return ResponseObj.success(map);
    }

    @RequestMapping(value = "cancel",method = RequestMethod.PATCH,produces = {"application/json;charset=utf-8"})
    public ResponseObj cancelOrder(@RequestBody @Valid CancelForm cancelForm, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        if (ResponsibleParty.getEnumsByCode(cancelForm.getResponsibleParty()) == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"请选择正确的责任方");
        }
        Integer userType = super.getUser().getType();
        if (!UserType.getEnumsByCode(userType).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        cancelForm.setFromApp(false);
        cancelForm.setUserType(userType);
        CancelOrderModel cancelOrderModel = cancelService.doCancel(cancelForm);
        return ResponseObj.success(cancelOrderModel);
    }

    @RequestMapping(value = "rent/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentList(RentWebListCondition condition) {
        if (!UserType.getEnumsByCode(super.getUser().getType()).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        Pager<RentInfo> rentListByPage = rentInfoService.getRentListForWebPage(condition);
        return ResponseObj.success(rentListByPage);
    }

    @RequestMapping(value = "rent/info/rentId/{rentId}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentDetail(@PathVariable(name = "rentId") Long rentId) {
        RentInfo rentInfo = rentInfoService.getRentInfo(rentId, null);
        return ResponseObj.success(rentInfo);
    }

    @RequestMapping(value = "trafficViolation/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getTrafficViolationList(TrafficViolationListCondition condition) {
        if (!UserType.getEnumsByCode(super.getUser().getType()).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        condition.setFromApp(false);
        Pager<OrderTrafficViolationInfo> trafficViolations = trafficViolationService.getTrafficViolations(condition);
        return ResponseObj.success(trafficViolations);
    }

    @RequestMapping(value = "trafficViolation/info/id/{id}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getTrafficViolationDetail(@PathVariable(name = "id") Long id) {
        OrderTrafficViolationInfo trafficViolationInfo = trafficViolationService.getTrafficViolationInfo(id);
        return ResponseObj.success(trafficViolationInfo);
    }

    @RequestMapping(value = "disobey/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getDisobeyList(DisobeyListCondition condition) {
        if (!UserType.getEnumsByCode(super.getUser().getType()).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        condition.setFromApp(false);
        Pager<OrderDisobeyInfo> disobeyList = disobeyService.getDisobeyList(condition);
        return ResponseObj.success(disobeyList);
    }

    @RequestMapping(value = "disobey/info/id/{id}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getDisobeyDetail(@PathVariable(name = "id") Long id) {
        OrderDisobeyInfo disobeyInfo = disobeyService.getDisobeyInfo(id);
        return ResponseObj.success(disobeyInfo);
    }

}
