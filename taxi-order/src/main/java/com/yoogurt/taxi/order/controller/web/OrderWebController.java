package com.yoogurt.taxi.order.controller.web;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderTrafficViolationInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.condition.order.RentWebListCondition;
import com.yoogurt.taxi.dal.condition.order.TrafficViolationListCondition;
import com.yoogurt.taxi.dal.enums.ResponsibleParty;
import com.yoogurt.taxi.dal.enums.TrafficStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.form.TrafficHandleForm;
import com.yoogurt.taxi.order.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CommonResourceService   commonResourceService;

    @RequestMapping(value = "list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getList(OrderListCondition condition) {
        if (!UserType.getEnumsByCode(super.getUser().getType()).isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        return ResponseObj.success(orderInfoService.getWebOrderList(condition));
    }

    @RequestMapping(value = "info/orderId/{orderId}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getDetail(@PathVariable(name = "orderId") String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED,"请指定订单号");
        }
        Map<String, Object> orderDetails = orderInfoService.getOrderDetails(orderId, null);
        List<OrderDisobeyInfo> disobeyList = disobeyService.getDisobeyList(orderId, null, null);
        if (CollectionUtils.isNotEmpty(disobeyList)) {
            orderDetails.put("disobeyList",disobeyList);
        }
        return ResponseObj.success(orderDetails);
    }

    @RequestMapping(value = "/resources/{linkId}/{name}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getResources(@PathVariable(name = "linkId") String linkId, @PathVariable(name = "name") String name) {

        if (StringUtils.isBlank(linkId)) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "请指定对象");
        }
        if (!"order_pick_up_info".equalsIgnoreCase(name) && !"order_accept_info".equalsIgnoreCase(name)) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "参数有误");
        }

//        StringBuilder urlBuilder = new StringBuilder();
        List<CommonResource> resources = commonResourceService.getResources(linkId, name);
//        for (int j=0,size=resources.size();j<size;j++) {
//            CommonResource resource = resources.get(j);
//            urlBuilder.append(resource.getUrl());
//            if(j != size - 1) urlBuilder.append(",");
//        }
        String stringStream = resources.stream().map(CommonResource::getUrl).collect(Collectors.joining(","));
        return ResponseObj.success(stringStream);
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
    public ResponseObj getRentDetail(@PathVariable(name = "rentId") String rentId) {
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

    /**
     * 处理违章
     * @param disobeyForm   表单参数
     * @param result        操作结果
     * @return
     */
    @RequestMapping(value = "trafficViolation",method = RequestMethod.PATCH,produces = {"application/json;charset=utf-8"})
    public ResponseObj trafficHandle(@Valid @RequestBody TrafficHandleForm disobeyForm, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        TrafficStatus trafficStatus;
        if (disobeyForm.isStatus()) {
            trafficStatus = TrafficStatus.NORMAL;
        }else {
            trafficStatus = TrafficStatus.WRONG;
        }
        OrderTrafficViolationInfo info = trafficViolationService.modifyStatus(disobeyForm.getId(), trafficStatus);
        return info != null ? ResponseObj.success(info) : ResponseObj.fail();
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
