package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.enums.ResponsibleParty;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.*;
import com.yoogurt.taxi.order.form.*;
import com.yoogurt.taxi.order.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mobile/order")
public class OrderMobileController extends BaseController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private HandoverService handoverService;

    @Autowired
    private PickUpService pickUpService;

    @Autowired
    private GiveBackService giveBackService;

    @Autowired
    private AcceptService acceptService;

    @Autowired
    private CancelService cancelService;

    @Autowired
    private RentInfoService rentInfoService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getOrderList(OrderListCondition condition) {
        if(!condition.validate()) return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        SessionUser user = super.getUser();
        condition.setUserId(user.getUserId());
        condition.setUserType(user.getType());
        condition.setFromApp(true);
        return ResponseObj.success(orderInfoService.getOrderList(condition));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj placeOrder(@Valid @RequestBody PlaceOrderForm orderForm, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        SessionUser user = super.getUser();
        orderForm.setUserId(user.getUserId());
        orderForm.setUserType(user.getType());
        return orderInfoService.placeOrder(orderForm);
    }

    @RequestMapping(value = "/info/{orderId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getOrderDetails(@PathVariable(name = "orderId") Long orderId) {

        return ResponseObj.success(orderInfoService.getOrderDetails(orderId, super.getUserId()));
    }

    @RequestMapping(value = "/handover", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doHandover(@Valid @RequestBody HandoverForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (!UserType.USER_APP_OFFICE.getCode().equals(super.getUser().getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "只有正式司机才能进行交车操作");
        }
        HandoverOrderModel model = handoverService.doHandover(form);
        if (model != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(model, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "交车出现问题，请稍后再试");
    }

    @RequestMapping(value = "/pickup", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doPickUp(@Valid @RequestBody PickUpForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (!UserType.USER_APP_AGENT.getCode().equals(super.getUser().getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "只有代理司机才能进行取车操作");
        }
        PickUpOrderModel model = pickUpService.doPickUp(form);
        if (model != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(model, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "取车出现问题，请稍后再试");
    }

    @RequestMapping(value = "/giveback", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doGiveBack(@Valid @RequestBody GiveBackForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (!UserType.USER_APP_AGENT.getCode().equals(super.getUser().getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "只有代理司机才能进行还车操作");
        }
        GiveBackOrderModel model = giveBackService.doGiveBack(form);
        if (model != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(model, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "还车出现问题，请稍后再试");
    }

    @RequestMapping(value = "/accept", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doAccept(@Valid @RequestBody AcceptForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (!UserType.USER_APP_OFFICE.getCode().equals(super.getUser().getType())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "只有正式司机才能进行收车操作");
        }
        AcceptOrderModel model = acceptService.doAccept(form);
        if (model != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(model, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "收车出现问题，请稍后再试");
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doAccept(@Valid @RequestBody CancelForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        form.setFromApp(true);
        SessionUser user = super.getUser();
        form.setResponsibleParty(ResponsibleParty.getEnumsByType(user.getType()).getCode());
        form.setReason(UserType.USER_APP_OFFICE.getCode().equals(user.getType()) ? "正式司机手动取消" : "代理司机手动取消");

        CancelOrderModel model = cancelService.doCancel(form);
        if (model != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(model, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "取消订单出现问题，请稍后再试");
    }

    @RequestMapping(value = "/cancel/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getCancelOrders() {
        OrderListCondition condition = new OrderListCondition();
        condition.setUserId(super.getUserId());
        condition.setStatus(OrderStatus.CANCELED.getCode());
        SessionUser user = super.getUser();
        List<RentInfo> rentInfoList = rentInfoService.getRentInfoList(user.getUserId(), null, null, RentStatus.CANCELED.getCode(), RentStatus.TIMEOUT.getCode());
        List<CancelModel> cancelList = orderInfoService.getCancelOrders(null, user.getUserId(), user.getType());
        rentInfoList.forEach(rent -> {
            CancelModel model = new CancelModel();
            BeanUtils.copyProperties(rent, model);
            model.setOrderId(rent.getRentId());
            model.setCancelTime(rent.getGmtModify());
            model.setReason("发布人取消订单");
            model.setOrderTime(rent.getGmtCreate());
            model.setAmount(rent.getPrice());
            cancelList.add(model);
        });
        return ResponseObj.success(cancelList);
    }
}
