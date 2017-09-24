package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.OrderDao;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private RestUserService userService;

    @Autowired
    private OrderDao orderDao;

    @Override
    public OrderModel placeOrder(PlaceOrderForm orderForm) {
        OrderInfo orderInfo = buildOrderInfo(orderForm);
        if (orderDao.insertSelective(orderInfo) == 1) {
            return getOrderInfo(orderInfo.getOrderId());
        }
        return null;
    }

    @Override
    public Pager<OrderModel> getOrderList(OrderListCondition condition) {
        return null;
    }

    @Override
    public OrderModel getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderDao.selectById(orderId);
        OrderModel model = new OrderModel();
        BeanUtils.copyProperties(orderInfo, model);
        return model;
    }

    @Override
    public OrderInfo buildOrderInfo(PlaceOrderForm orderForm) {
        RentInfo rentInfo = rentInfoService.getRentInfo(orderForm.getRentId());
        OrderInfo order = new OrderInfo(RandomUtils.getPrimaryKey());
        UserInfo userInfo = userService.getUserInfoById(orderForm.getUserId());
        DriverInfo driverInfo = userService.getDriverInfoByUserId(userInfo.getUserId());
        if (!rentInfo.getUserType().equals(userInfo.getType())) {
            if (rentInfo.getUserType().equals(UserType.USER_APP_AGENT.getCode())) {
                //代理司机发单，正式司机接单
                order.setAgentDriverId(rentInfo.getDriverId());
                order.setAgentDriverPhone(rentInfo.getMobile());
                order.setAgentDriverName(rentInfo.getDriverName());

                order.setOfficialDriverId(driverInfo.getId());
                order.setOfficialDriverPhone(driverInfo.getMobile());
                order.setOfficialDriverName(userInfo.getName());
            } else if (rentInfo.getUserType().equals(UserType.USER_APP_OFFICE.getCode())) {
                //正式司机发单，代理司机接单
                order.setOfficialDriverId(rentInfo.getDriverId());
                order.setOfficialDriverPhone(rentInfo.getMobile());
                order.setOfficialDriverName(rentInfo.getDriverName());

                order.setAgentDriverId(driverInfo.getId());
                order.setAgentDriverPhone(driverInfo.getMobile());
                order.setAgentDriverName(userInfo.getName());
            }
            buildRentInfo(order, rentInfo);
            buildCarInfo(order, rentInfo);
        }
        return order;
    }

    private void buildRentInfo(OrderInfo order, RentInfo rent) {
        order.setRentId(rent.getRentId());
        order.setHandoverTime(rent.getHandoverTime());
        order.setGiveBackTime(rent.getGiveBackTime());
        order.setAddress(rent.getAddress());
        order.setLng(rent.getLng());
        order.setLat(rent.getLat());
        order.setPrice(rent.getPrice());
    }

    private void buildDriverInfo(OrderInfo order, RentInfo rent) {

    }

    private void buildCarInfo(OrderInfo order, RentInfo rent) {
        order.setCarId(rent.getCarId());
        order.setPlateNumber(rent.getPlateNumber());
        order.setVehicleType(rent.getVehicleType());
        order.setCarThumb(rent.getCarThumb());
        order.setEnergyType(rent.getEnergyType());
        order.setVin(rent.getVin());
        order.setRemark(rent.getRemark());
    }
}
