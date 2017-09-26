package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.RestResult;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(orderInfo, model);
            return model;
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
        RestResult<UserInfo> userResult = userService.getUserInfoById(orderForm.getUserId());
        if (userResult.getStatus() != StatusCode.INFO_SUCCESS.getStatus()) {
            log.warn("[REST]{}", userResult.getMessage());
            return null;
        }
        UserInfo userInfo = userResult.getBody();
        RestResult<DriverInfo> driverResult = userService.getDriverInfoByUserId(userInfo.getUserId());
        if (driverResult.getStatus() != StatusCode.INFO_SUCCESS.getStatus()) {
            log.warn("[REST]{}", userResult.getMessage());
            return null;
        }
        DriverInfo driverInfo = driverResult.getBody();
        if (!rentInfo.getUserType().equals(userInfo.getType())) {
            buildDriverInfo(order, rentInfo, driverInfo, userInfo);
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

    private void buildCarInfo(OrderInfo order, RentInfo rent) {
        order.setCarId(rent.getCarId());
        order.setPlateNumber(rent.getPlateNumber());
        order.setVehicleType(rent.getVehicleType());
        order.setCarThumb(rent.getCarThumb());
        order.setEnergyType(rent.getEnergyType());
        order.setVin(rent.getVin());
        order.setRemark(rent.getRemark());
    }

    private void buildDriverInfo(OrderInfo order, RentInfo rent, DriverInfo driver, UserInfo user) {
        if (rent.getUserType().equals(UserType.USER_APP_AGENT.getCode())) {
            //代理司机发单，正式司机接单
            order.setAgentDriverId(rent.getDriverId());
            order.setAgentDriverPhone(rent.getMobile());
            order.setAgentDriverName(rent.getDriverName());

            order.setOfficialDriverId(driver.getId());
            order.setOfficialDriverPhone(driver.getMobile());
            order.setOfficialDriverName(user.getName());
        } else if (rent.getUserType().equals(UserType.USER_APP_OFFICE.getCode())) {
            //正式司机发单，代理司机接单
            order.setOfficialDriverId(rent.getDriverId());
            order.setOfficialDriverPhone(rent.getMobile());
            order.setOfficialDriverName(rent.getDriverName());

            order.setAgentDriverId(driver.getId());
            order.setAgentDriverPhone(driver.getMobile());
            order.setAgentDriverName(user.getName());
        }
    }

}
