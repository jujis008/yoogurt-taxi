package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.bo.DateTimeSection;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.*;
import com.yoogurt.taxi.order.dao.OrderDao;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.*;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service("orderInfoService")
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private RestUserService userService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private DisobeyService disobeyService;

    @Autowired
    private PagerFactory appPagerFactory;

    @Autowired
    private PagerFactory webPagerFactory;


    @Override
    public ResponseObj placeOrder(PlaceOrderForm orderForm) {
        ResponseObj obj = buildOrderInfo(orderForm);
        OrderInfo orderInfo = (OrderInfo) obj.getBody();
        if (obj.isSuccess() && orderDao.insertSelective(orderInfo) == 1) {
            //更改租单状态 --> 已接单
            rentInfoService.modifyStatus(orderForm.getRentId(), RentStatus.RENT);
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(orderInfo, model);
            model.setOrderTime(orderInfo.getGmtCreate());
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
    public OrderInfo getOrderInfo(Long orderId, Long userId) {
        OrderInfo orderInfo = orderDao.selectById(orderId);
        if(orderInfo == null) return null;
        //用户id不符合
        if (userId != null && !userId.equals(orderInfo.getAgentUserId()) && !userId.equals(orderInfo.getOfficialUserId())) {
            return null;
        }
        return orderInfo;
    }


    @Override
    public OrderModel info(Long orderId, Long userId) {
        OrderModel model = new OrderModel();
        OrderInfo orderInfo = getOrderInfo(orderId, userId);
        if (orderInfo != null) {
            BeanUtils.copyProperties(orderInfo, model);
            model.setOrderTime(orderInfo.getGmtCreate());
        }
        return model;
    }

    /**
     * 订单详情接口。
     * 需要获取当前状态以及之前的所有状态下的子订单信息。
     * @param orderId 订单id
     * @param userId 用户id
     * @return 订单详细信息。
     * <p>
     *     key: baseOrderModel表示订单的基本信息，对应的value是一个由OrderModel转换成的Map对象；
     *     key：各个子状态model的类名(simpleName)，首字母小写，对应的value是一个由子Model转换成的Map对象；
     *     key: disobeys表示违约记录，对应的value是一个数组；
     *     key: trafficViolations表示违章记录
     * </p>
     */
    @Override
    public Map<String, Object> getOrderDetails(Long orderId, Long userId) {
        OrderInfo orderInfo = getOrderInfo(orderId, userId);
        if (orderInfo == null) return null;
        Map<String, Object> result = Maps.newHashMap();

        //构造主订单的信息
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderInfo, orderModel);
        orderModel.setOrderTime(orderInfo.getGmtCreate());
        result.put("baseOrderModel", BeanRefUtils.toMap(orderModel, true));

        //向上追溯，将之前的订单流转信息记录下来
        OrderStatus previous = OrderStatus.getEnumsByCode(orderInfo.getStatus()).previous();
        while (previous != null) {

            //根据订单状态生成对应的model，此时对象的属性还未注入
            OrderModel model = getOrderModel(previous);
            previous = previous.previous();
            if(model == null) continue;
            //根据名称，获取service
            //这里需要子订单的各个service继承OrderBizService接口
            OrderBizService service = (OrderBizService) context.getBean(model.getServiceName());
            OrderModel info = service.info(orderId, userId);
            String name = info.getClass().getSimpleName();
            String firstLetter = StringUtils.left(name, 1);
            //类名，第一个字母转换成小写，作为返回结果的key
            String key = name.replaceFirst(firstLetter, firstLetter.toLowerCase(Locale.ENGLISH));
            result.put(key, BeanRefUtils.toMap(info, false));
        }
        //违约记录
        DisobeyListCondition condition = new DisobeyListCondition();
        condition.setOrderId(orderId);
        List<OrderDisobeyInfo> disobeyList = disobeyService.getDisobeyList(orderId, null);
        result.put("disobeys", disobeyList);

        return result;
    }

    /**
     * 修改订单状态
     *
     * @param orderId 订单id
     * @param status  修改后的订单状态
     * @return true-修改成功，false-修改失败
     */
    @Override
    public boolean modifyStatus(Long orderId, OrderStatus status) {
        if(orderId == null || orderId <= 0 || status == null) return false;
        OrderInfo orderInfo = getOrderInfo(orderId, null);
        if(orderInfo == null) return false;
        if(status.getCode().equals(orderInfo.getStatus())) return true; //与原租单状态相同，直接返回true
        if(status.getCode() < orderInfo.getStatus()) return false; //不允许状态回退

        orderInfo.setStatus(status.getCode());
        return saveOrderInfo(orderInfo, false) != null;
    }

    /**
     * 保存订单信息，有订单ID则更新，否则插入
     *
     * @param orderInfo 订单信息
     * @param add 是否插入
     * @return 保存后的订单信息
     */
    @Override
    public OrderInfo saveOrderInfo(OrderInfo orderInfo, boolean add) {
        if(orderInfo == null) return null;
        if (add) {
            if(orderDao.insertSelective(orderInfo) != 1) return null;
        } else {
            if(orderDao.updateByIdSelective(orderInfo) != 1) return null;
        }
        return orderInfo;
    }

    /**
     * 指定用户id和订单状态，获取相应的订单列表，并按交车时间升序排列。
     * @param orderForm 用户id
     * @param status 订单状态，可以传入多个
     * @return 租单列表
     */
    private List<OrderInfo> getOrderList(PlaceOrderForm orderForm, Integer... status) {
        Example ex = new Example(OrderInfo.class);
        Example.Criteria criteria = ex.createCriteria()
                .andIn("status", Arrays.asList(status))
                .andEqualTo("isDeleted", Boolean.FALSE);
        if (UserType.USER_APP_AGENT.getCode().equals(orderForm.getUserType())) {
            criteria.andEqualTo("agentUserId", orderForm.getUserId());
        } else {
            criteria.andEqualTo("officialUserId", orderForm.getUserId());
        }
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
     * @param orderForm 下单用户ID
     * @return 校验结果
     */
    private ResponseObj isAllowOrder(RentInfo rentInfo, PlaceOrderForm orderForm) {

        //1. 租单状态
        if (rentInfo == null) {
            log.warn("租单信息不存在");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "租单信息不存在");
        }
        if (rentInfo.getUserType().equals(orderForm.getUserType())) {
            log.warn("接单人和发单人两者的用户类型不能一样");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "接单人和发单人两者的用户类型不能一样");
        }
        if (RentStatus.CANCELED.getCode().equals(rentInfo.getStatus())) {
            log.warn("该租单已取消，无法接单");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该租单已取消，无法接单");
        }
        if (!RentStatus.WAITING.getCode().equals(rentInfo.getStatus())) {
            log.warn("该租单信息已被他人接走");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该租单信息已被他人接走");
        }
        //TODO 2. 押金余额 "extras": {"redirect": "charge"}


        DateTimeSection section = new DateTimeSection(rentInfo.getHandoverTime(), rentInfo.getGiveBackTime());
        //3. 未完成订单数量不超过上限
        List<OrderInfo> orderList = getOrderList(orderForm, OrderStatus.HAND_OVER.getCode(), OrderStatus.PICK_UP.getCode(), OrderStatus.GIVE_BACK.getCode(), OrderStatus.ACCEPT.getCode());
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
        //校验是否满足下单条件
        ResponseObj validateResult = isAllowOrder(rentInfo, orderForm);
        //下单校验未通过
        if(!validateResult.isSuccess()) return validateResult;
        Long userId = orderForm.getUserId();
        //租单信息不包含车辆，说明是代理司机发布的求租信息
        if (rentInfo.getCarId() == null) {
            RestResult<List<CarInfo>> carResult = userService.getCarInfoByUserId(userId);
            if (!carResult.isSuccess()) {
                log.warn("[REST]{}", carResult.getMessage());
                return ResponseObj.fail(StatusCode.BIZ_FAILED, carResult.getMessage());
            } else if (CollectionUtils.isEmpty(carResult.getBody())) {
                log.warn("[REST]{}", "找不到出租车辆信息");
                return ResponseObj.fail(StatusCode.BIZ_FAILED, "找不到出租车辆信息");
            }
            CarInfo carInfo = carResult.getBody().get(0);
            buildCarInfo(rentInfo, carInfo);
        }
        OrderInfo order = new OrderInfo(RandomUtils.getPrimaryKey());
        RestResult<UserInfo> userResult = userService.getUserInfoById(userId);
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
        buildDriverInfo(order, rentInfo, driverInfo, userInfo);
        buildCarInfo(order, rentInfo);
        buildRentInfo(order, rentInfo);
        return ResponseObj.success(order);
    }

    private void buildRentInfo(OrderInfo order, RentInfo rent) {
        order.setRentId(rent.getRentId());
        order.setHandoverTime(rent.getHandoverTime());
        order.setGiveBackTime(rent.getGiveBackTime());
        order.setAddress(rent.getAddress());
        order.setLng(rent.getLng());
        order.setLat(rent.getLat());
        order.setPrice(rent.getPrice());
        order.setAmount(rent.getPrice());
    }

    private void buildCarInfo(OrderInfo order, RentInfo rent) {
        order.setCarId(rent.getCarId());
        order.setCompany(rent.getCompany());
        order.setPlateNumber(rent.getPlateNumber());
        order.setVehicleType(rent.getVehicleType());
        order.setCarThumb(rent.getCarThumb());
        order.setEnergyType(rent.getEnergyType());
        order.setVin(rent.getVin());
        order.setRemark(rent.getRemark());
    }

    private void buildCarInfo(RentInfo rent, CarInfo car) {
        rent.setCarId(car.getId());
        rent.setCompany(car.getCompany());
        rent.setPlateNumber(car.getPlateNumber());
        rent.setVehicleType(car.getVehicleType());
        rent.setEnergyType(car.getEnergyType());
        rent.setCarThumb(car.getCarPicture());
        rent.setVin(car.getVin());
    }

    private void buildDriverInfo(OrderInfo order, RentInfo rent, DriverInfo driver, UserInfo user) {
        if (rent.getUserType().equals(UserType.USER_APP_AGENT.getCode())) {
            //代理司机发单，正式司机接单
            order.setAgentUserId(rent.getUserId());
            order.setAgentDriverId(rent.getDriverId());
            order.setAgentDriverPhone(rent.getMobile());
            order.setAgentDriverName(rent.getDriverName());

            order.setOfficialUserId(user.getUserId());
            order.setOfficialDriverName(user.getName());
            order.setOfficialDriverId(driver.getId());
            order.setOfficialDriverPhone(driver.getMobile());
        } else if (rent.getUserType().equals(UserType.USER_APP_OFFICE.getCode())) {
            //正式司机发单，代理司机接单
            order.setOfficialUserId(rent.getUserId());
            order.setOfficialDriverId(rent.getDriverId());
            order.setOfficialDriverPhone(rent.getMobile());
            order.setOfficialDriverName(rent.getDriverName());

            order.setAgentUserId(user.getUserId());
            order.setAgentDriverName(user.getName());
            order.setAgentDriverId(driver.getId());
            order.setAgentDriverPhone(driver.getMobile());
        }
    }

    /**
     * 根据订单状态生成对应的Model
     * @param status 订单状态
     * @return OrderModel
     */
    private OrderModel getOrderModel(OrderStatus status) {
        if(status == null) return null;

        switch (status) {
            case PICK_UP:
                return new PickUpOrderModel();
            case GIVE_BACK:
                return new GiveBackOrderModel();
            case ACCEPT:
                return new AcceptOrderModel();
            case CANCELED:
                return new CancelOrderModel();
            default:
                return null;
        }
    }
}
