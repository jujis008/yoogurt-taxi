package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.condition.order.WarningOrderCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.CancelModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.form.PlaceOrderForm;

import java.util.List;
import java.util.Map;

public interface OrderInfoService extends OrderBizService {

    /**
     * 接单
     */
    ResponseObj placeOrder(PlaceOrderForm orderForm);

    /**
     * 分页获取订单列表
     *
     * @param condition 查询条件
     * @return 订单列表
     */
    Pager<OrderModel> getOrderList(OrderListCondition condition);

    /**
     *
     * @param condition 查询条件
     * @author wudeyou
     * @return 后台订单列表
     */
    Pager<OrderModel> getWebOrderList(OrderListCondition condition);

    /**
     * 获取取消订单列表
     *
     * @param orderId  订单ID
     * @param userId   用户ID
     * @param userType 用户类型
     * @return 取消订单列表
     */
    List<CancelModel> getCancelOrders(Long orderId, Long userId, Integer userType);

    /**
     * 获取订单列表
     *
     * @param userId   用户id
     * @param userType 用户类型
     * @param status   用户状态
     * @return 订单列表
     */
    List<OrderInfo> getOrderList(Long userId, Integer userType, Integer... status);

    /**
     * 获取告警订单列表
     *
     * @param condition 查询条件
     * @return 告警订单列表
     */
    List<OrderInfo> getWarningOrders(WarningOrderCondition condition);

    /**
     * 获取订单基本信息
     *
     * @param orderId 订单id
     * @param userId  用户ID，传入不为空时，表示要验证订单的所有者
     * @return 订单基本信息，返回null表示订单不存在
     */
    OrderInfo getOrderInfo(Long orderId, Long userId);

    /**
     * 订单详情接口
     *
     * @param orderId 订单id
     * @param userId  用户id
     * @return 订单详细信息
     */
    Map<String, Object> getOrderDetails(Long orderId, Long userId);

    /**
     * 修改订单状态
     *
     * @param orderId 订单id
     * @param status  修改后的订单状态
     * @return true-修改成功，false-修改失败
     */
    boolean modifyStatus(Long orderId, OrderStatus status);

    /**
     * 保存订单信息
     *
     * @param orderInfo 订单信息
     * @param add       是否插入
     * @return 保存后的订单信息
     */
    OrderInfo saveOrderInfo(OrderInfo orderInfo, boolean add);

    /**
     *  将订单修改成已支付
     * @param orderId 订单id
     */
    void modifyPayStatus(Long orderId);
}
