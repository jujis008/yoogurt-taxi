package com.yoogurt.taxi.order.controller.rest;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.dal.beans.OrderStatistic;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/order")
public class RestOrderController {

    @Autowired
    private OrderStatisticService orderStatisticService;

    @Autowired
    private CommentTagStatisticService commentTagStatisticService;

    @RequestMapping(value = "/statistics/userId/{userId}",method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj statistics(@PathVariable(name = "userId") Long userId) {

        //订单统计信息
        OrderStatistic orderStatistics = orderStatisticService.getOrderStatistics(userId);
        //评价标签统计信息
        List<CommentTagStatistic> commentTagStatistics = commentTagStatisticService.getStatistic(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("order", orderStatistics);
        result.put("comment", commentTagStatistics);
        return ResponseObj.success(result);
    }
}
