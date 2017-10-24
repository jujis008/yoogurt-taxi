package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.order.service.CancelRuleService;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mobile/order")
public class DisobeyRuleController extends BaseController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CancelRuleService cancelRuleService;

    @Autowired
    private HandoverRuleService handoverRuleService;

    @Autowired
    private GiveBackRuleService giveBackRuleService;

    @RequestMapping(value = "/disobey/descriptions/{type}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getDescription(@PathVariable(name = "type") String type) {

        if ("cancel".equalsIgnoreCase(type)) {
            return ResponseObj.success(cancelRuleService.getIntroduction());
        } else if ("handover".equalsIgnoreCase(type)) {
            return ResponseObj.success(handoverRuleService.getIntroduction());
        } else if ("giveback".equalsIgnoreCase(type)) {
            return ResponseObj.success(giveBackRuleService.getIntroduction());
        } else {
            return ResponseObj.fail();
        }
    }

    @RequestMapping(value = "/disobey/rules/{orderId}/{type}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRule(@PathVariable(name = "orderId") Long orderId, @PathVariable(name = "type") String type) {
        Map<String, Object> extras = new HashMap<>();
        extras.put("type", type);
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, super.getUserId());
        if(orderInfo == null) return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单不存在");
        long period = orderInfo.getHandoverTime().getTime() - new Date().getTime();
        if ("cancel".equalsIgnoreCase(type)) {

            OrderCancelRule rule = cancelRuleService.getRuleInfo(period);
            extras.put("exists", rule != null);
            return ResponseObj.success(rule, extras);
        } else if ("handover".equalsIgnoreCase(type)) {

            OrderHandoverRule rule = handoverRuleService.getRuleInfo(period);
            extras.put("exists", rule != null);
            return ResponseObj.success(rule, extras);
        } else if ("giveback".equalsIgnoreCase(type)) {

            OrderGiveBackRule rule = giveBackRuleService.getRuleInfo(period);
            extras.put("exists", rule != null);
            return ResponseObj.success(rule, extras);
        } else {
            return ResponseObj.fail();
        }
    }
}
