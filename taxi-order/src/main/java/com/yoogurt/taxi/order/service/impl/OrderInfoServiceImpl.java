package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.bo.DateTimeSection;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.OrderDao;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
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

    @Autowired
    private PagerFactory appPagerFactory;

    @Autowired
    private PagerFactory webPagerFactory;


    @Override
    public ResponseObj placeOrder(PlaceOrderForm orderForm) {
        ResponseObj obj = buildOrderInfo(orderForm);
        if (obj.isSuccess() && orderDao.insertSelective((OrderInfo) obj.getBody()) == 1) {
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(obj, model);
            return ResponseObj.success(model);
        }
        return obj;
    }

    @Override
    public Pager<OrderModel> getOrderList(OrderListCondition condition) {

        Page<OrderModel> orders = orderDao.getOrderList(condition);

        if(!condition.isFromApp()){
            return webPagerFactory.generatePager(orders);
        }
        return appPagerFactory.generatePager(orders);
    }

    @Override
    public OrderModel getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderDao.selectById(orderId);
        OrderModel model = new OrderModel();
        BeanUtils.copyProperties(orderInfo, model);
        return model;
    }

    /**
     * 指定用户id和订单状态，获取相应的订单列表，并按交车时间升序排列。
     * @param userId 用户id
     * @param status 订单状态，可以传入多个
     * @return 租单列表
     */
    private List<OrderInfo> getOrderList(Long userId, Integer... status) {
        Example ex = new Example(OrderInfo.class);
        ex.createCriteria().andEqualTo("userId", userId)
                .andIn("status", Arrays.asList(status))
                .andEqualTo("isDeleted", Boolean.FALSE);
        ex.setOrderByClause("handover_time ASC");
        return orderDao.selectByExample(ex);
    }

    /**
     * 校验是否可以下单。
     * 1、租单信息：未接单
     * 2、押金余额：充足
     * 3、未完成订单数量不超过上限
     * 4、交易时间不重叠
     * @param rentInfo 租单信息
     * @param orderUserId 下单用户ID
     * @return 校验结果
     */
    private ResponseObj isAllowOrder(RentInfo rentInfo, Long orderUserId) {

        //1. 租单状态
        if (rentInfo == null) {
            log.warn("租单信息不存在");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "租单信息不存在");
        }
        if (!RentStatus.WAITING.getCode().equals(rentInfo.getStatus())) {
            log.warn("该租单信息已被他人接单");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该租单信息已被他人接单");
        }
        //TODO 2. 押金余额


        DateTimeSection section = new DateTimeSection(rentInfo.getHandoverTime(), rentInfo.getGiveBackTime());
        //3. 未完成订单数量不超过上限
        List<OrderInfo> orderList = getOrderList(orderUserId, OrderStatus.HAND_OVER.getCode(), OrderStatus.PICK_UP.getCode(), OrderStatus.GIVE_BACK.getCode(), OrderStatus.ACCEPT.getCode());
        if(CollectionUtils.size(orderList) >= Constants.MAX_RENT_COUNT)
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "最多发布" + Constants.MAX_RENT_COUNT + "笔租单");

        //4. 交易时间不重叠
        for (OrderInfo orderInfo : orderList) {
            DateTimeSection sectionA = new DateTimeSection(orderInfo.getHandoverTime(), orderInfo.getGiveBackTime());
            if (DateTimeSection.isInclude(sectionA, section)) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED, sectionA.toString() + "有订单未完成");
            }
        }

        return ResponseObj.success();
    }

    private ResponseObj buildOrderInfo(PlaceOrderForm orderForm) {
        RentInfo rentInfo = rentInfoService.getRentInfo(orderForm.getRentId(), null);
        ResponseObj validateResult = isAllowOrder(rentInfo, orderForm.getUserId());
        //下单校验未通过
        if(!validateResult.isSuccess()) return validateResult;

        OrderInfo order = new OrderInfo(RandomUtils.getPrimaryKey());
        RestResult<UserInfo> userResult = userService.getUserInfoById(orderForm.getUserId());
        if (!userResult.isSuccess()) {
            log.warn("[REST]{}", userResult.getMessage());
            return ResponseObj.fail(StatusCode.BIZ_FAILED, userResult.getMessage());
        }
        UserInfo userInfo = userResult.getBody();
        RestResult<DriverInfo> driverResult = userService.getDriverInfoByUserId(userInfo.getUserId());
        if (!driverResult.isSuccess()) {
            log.warn("[REST]{}", driverResult.getMessage());
            return ResponseObj.fail(StatusCode.BIZ_FAILED, driverResult.getMessage());
        }
        DriverInfo driverInfo = driverResult.getBody();
        if (!rentInfo.getUserType().equals(userInfo.getType())) {
            buildDriverInfo(order, rentInfo, driverInfo, userInfo);
            buildRentInfo(order, rentInfo);
            buildCarInfo(order, rentInfo);
            return ResponseObj.success(order);
        }
        log.warn("接单人和发单人两者的用户类型不能一样");
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "接单人和发单人两者的用户类型不能一样");
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
