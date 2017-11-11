package com.yoogurt.taxi.order.controller.rest;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/order")
public class RestOrderController extends BaseController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderStatisticService orderStatisticService;

    @Autowired
    private CommentTagStatisticService commentTagStatisticService;

    @RequestMapping(value = "/statistics/userId/{userId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public RestResult<Map<String, Object>> statistics(@PathVariable(name = "userId") String userId) {

        //订单统计信息
        OrderStatistic orderStatistics = orderStatisticService.getOrderStatistics(userId);
        //评价标签统计信息
        List<CommentTagStatistic> commentTagStatistics = commentTagStatisticService.getStatistic(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("order", orderStatistics);
        result.put("comment", commentTagStatistics);
        return RestResult.success(result);
    }

    @RequestMapping(value = "/statistics/userId/{userId}/userType/{userType}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public RestResult<Map<String, Object>> orderStatistics(@PathVariable(name = "userId") String userId, @PathVariable(name = "userType") Integer userType) {
        Map<String, Object> map = new HashMap<>();

        //未完成订单数量
        //最近一笔已完成订单时间
        List<OrderInfo> orders = orderInfoService.getOrderList(userId, userType);
        List<OrderInfo> finishedList = new ArrayList<>();
        List<OrderInfo> unfinishedList = new ArrayList<>();
        orders.forEach(order -> {
            if (OrderStatus.FINISH.getCode().equals(order.getStatus())) {
                finishedList.add(order);
            } else if (!OrderStatus.CANCELED.getCode().equals(order.getStatus())) {
                unfinishedList.add(order);
            }
        });
        //已完成订单
        map.put("finishedList", finishedList);
        //未完成的订单列表
        map.put("unfinishedList", unfinishedList);
        //是否有未完成的订单
        map.put("hasUnFinishedOrder", CollectionUtils.isNotEmpty(unfinishedList));
        //当前时间戳
        map.put("timestamp", System.currentTimeMillis());

        return RestResult.success(map);
    }
}
