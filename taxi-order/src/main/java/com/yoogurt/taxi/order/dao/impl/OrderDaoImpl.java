package com.yoogurt.taxi.order.dao.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.mapper.OrderInfoMapper;
import com.yoogurt.taxi.dal.model.order.CancelModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDaoImpl extends BaseDao<OrderInfoMapper, OrderInfo> implements OrderDao{

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public Page<OrderModel> getOrderList(OrderListCondition condition) {

        PageHelper.startPage(condition.getPageNum(), condition.getPageSize(), "gmt_modify DESC, gmt_create DESC");
        return orderInfoMapper.getOrderList(condition.getOrderId(), condition.getUserId(), condition.getUserType(),
                condition.getPhone(), condition.getDriverName(), condition.getStatus(),
                condition.getStartTime(), condition.getEndTime());
    }

    @Override
    public List<CancelModel> getCancelOrders(Long orderId, Long userId, Integer userType) {
        return orderInfoMapper.getCancelOrders(orderId, userId, userType);
    }

}
