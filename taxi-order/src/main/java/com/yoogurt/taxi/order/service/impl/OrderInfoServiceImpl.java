package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.yoogurt.taxi.common.bo.DateTimeSection;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.utils.BeanRefUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.condition.order.WarningOrderCondition;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.order.*;
import com.yoogurt.taxi.order.dao.OrderDao;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import com.yoogurt.taxi.order.service.OrderBizService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service("orderInfoService")
public class OrderInfoServiceImpl extends AbstractOrderBizService implements OrderInfoService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private RestUserService userService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private HandoverRuleService handoverRuleService;

    @Autowired
    private PagerFactory appPagerFactory;

    @Autowired
    private PagerFactory webPagerFactory;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * 下单专用锁
     */
    private ReentrantLock lock = new ReentrantLock();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseObj placeOrder(PlaceOrderForm orderForm) {
        boolean success = false;
        OrderInfo orderInfo;
        ResponseObj obj;
        lock.lock();
        try {
            obj = buildOrderInfo(orderForm);
            if (obj.isSuccess()) {
                orderInfo = (OrderInfo) obj.getBody();
                if (orderDao.insertSelective(orderInfo) == 1) {
                    //更改租单状态 --> 已接单
                    String rentId = orderForm.getRentId();
                    success = rentInfoService.modifyStatus(rentId, RentStatus.RENT);
                }
            } else {// 下单校验不成功，直接返回
                return obj;
            }
        } finally {
            lock.unlock();
        }
        if (orderInfo != null && success) {
            //更改租单状态 --> 已接单
            String rentId = orderForm.getRentId();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime handoverDateTime = LocalDateTime.ofInstant(orderInfo.getHandoverTime().toInstant(), ZoneId.systemDefault());
            long durationSeconds = Duration.between(now, handoverDateTime).getSeconds();

            redisHelper.del(CacheKey.MESSAGE_ORDER_TIMEOUT_KEY + rentId);
            //设置交车前1小时提醒任务
            //如果接单时，距离交车时间不足一小时，就不用发通知了
            if (durationSeconds - 3600 > 10) {
                redisHelper.set(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER1_KEY + rentId, rentId, durationSeconds - 3600);
            }

            //设置交车到点提醒任务
            redisHelper.set(CacheKey.MESSAGE_ORDER_HANDOVER_REMINDER_KEY + rentId, rentId, durationSeconds);

            //设置交车超时，最大违约任务
            OrderHandoverRule rule = handoverRuleService.getRuleInfo();
            BigDecimal divide = orderInfo.getAmount().divide(rule.getPrice(), BigDecimal.ROUND_FLOOR);
            long seconds = (TimeUnit.valueOf(rule.getUnit()).toSeconds(divide.longValue()) + durationSeconds);
            redisHelper.set(CacheKey.MESSAGE_ORDER_HANDOVER_UNFINISHED_REMINDER_KEY + rentId, rentId, seconds);
            //已接单，通知对方
            super.push(orderInfo, UserType.getEnumsByCode(orderForm.getUserType()).equals(UserType.USER_APP_AGENT) ? UserType.USER_APP_OFFICE : UserType.USER_APP_AGENT, SendType.ORDER_RENT, new HashMap<>(1));
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(orderInfo, model);
            model.setOrderTime(orderInfo.getGmtCreate());
            return ResponseObj.success(model);
        }
        return obj;
    }

    @Override
    public BasePager<OrderModel> getOrderList(OrderListCondition condition) {

        Page<OrderModel> orders = orderDao.getOrderList(condition);

        if (!condition.isFromApp()) {
            return webPagerFactory.generatePager(orders);
        }
        return appPagerFactory.generatePager(orders);
    }

    /**
     * @param condition 查询条件
     * @return 后台订单列表
     * @author wudeyou
     */
    @Override
    public BasePager<OrderModel> getWebOrderList(OrderListCondition condition) {
        Page<OrderModel> webOrderList = orderDao.getWebOrderList(condition);
        return webPagerFactory.generatePager(webOrderList);
    }

    /**
     * 获取取消订单列表
     *
     * @param orderId  订单ID
     * @param userId   用户ID
     * @param userType 用户类型
     * @return 取消订单列表
     */
    @Override
    public List<CancelModel> getCancelOrders(String orderId, String userId, Integer userType) {

        return orderDao.getCancelOrders(orderId, userId, userType);
    }

    @Override
    public OrderInfo getOrderInfo(String orderId, String userId) {
        OrderInfo orderInfo = orderDao.selectById(orderId);
        if (orderInfo == null) {
            return null;
        }
        //用户id不符合
        if (StringUtils.isNotBlank(userId) && !userId.equals(orderInfo.getAgentUserId()) && !userId.equals(orderInfo.getOfficialUserId())) {
            return null;
        }
        return orderInfo;
    }


    @Override
    public OrderModel info(String orderId, String userId) {
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
     *
     * @param orderId 订单id
     * @param userId  用户id
     * @return 订单详细信息。
     * <p>
     * key: baseOrderModel表示订单的基本信息，对应的value是一个由OrderModel转换成的Map对象；
     * key：各个子状态model的类名(simpleName)，首字母小写，对应的value是一个由子Model转换成的Map对象；
     * key: disobeys表示违约记录，对应的value是一个数组；
     * key: trafficViolations表示违章记录
     * </p>
     */
    @Override
    public Map<String, Object> getOrderDetails(String orderId, String userId) {
        OrderInfo orderInfo = getOrderInfo(orderId, userId);
        if (orderInfo == null) {
            return null;
        }
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
            if (model == null) {
                continue;
            }
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
    public boolean modifyStatus(String orderId, OrderStatus status) {
        if (StringUtils.isBlank(orderId) || status == null) {
            return false;
        }
        OrderInfo orderInfo = getOrderInfo(orderId, null);
        if (orderInfo == null) {
            return false;
        }
        if (status.getCode().equals(orderInfo.getStatus())) {
            //与原租单状态相同，直接返回true
            return true;
        }
        if (status.getCode() < orderInfo.getStatus()) {
            //不允许状态回退
            return false;
        }

        orderInfo.setStatus(status.getCode());
        return saveOrderInfo(orderInfo, false) != null;
    }

    /**
     * 保存订单信息，有订单ID则更新，否则插入
     *
     * @param orderInfo 订单信息
     * @param add       是否插入
     * @return 保存后的订单信息
     */
    @Override
    public OrderInfo saveOrderInfo(OrderInfo orderInfo, boolean add) {
        if (orderInfo == null) {
            return null;
        }
        if (add) {
            if (orderDao.insertSelective(orderInfo) != 1) {
                return null;
            }
        } else {
            if (orderDao.updateByIdSelective(orderInfo) != 1) {
                return null;
            }
        }
        return orderInfo;
    }

    @Override
    public void modifyPayStatus(String orderId) {
        OrderInfo orderInfo = getOrderInfo(orderId, null);
        if (orderInfo != null) {
            orderInfo.setIsPaid(true);
            saveOrderInfo(orderInfo, false);
        }
    }

    /**
     * 指定用户id和订单状态，获取相应的订单列表，并按交车时间升序排列。
     *
     * @param userId   用户id
     * @param userType 用户类型
     * @param status   订单状态，可以传入多个
     * @return 租单列表
     */
    @Override
    public List<OrderInfo> getOrderList(String userId, Integer userType, Integer... status) {
        Example ex = new Example(OrderInfo.class);
        Example.Criteria criteria = ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        if (status != null && status.length > 0) {
            criteria.andIn("status", Arrays.asList(status));
        }
        if (UserType.USER_APP_AGENT.getCode().equals(userType)) {
            criteria.andEqualTo("agentUserId", userId);
        } else {
            criteria.andEqualTo("officialUserId", userId);
        }
        ex.setOrderByClause("handover_time ASC");
        return orderDao.selectByExample(ex);
    }

    /**
     * 获取告警订单列表
     *
     * @param condition 查询条件
     * @return 告警订单列表
     */
    @Override
    public List<OrderInfo> getWarningOrders(WarningOrderCondition condition) {

        Example ex = new Example(OrderInfo.class);
        ex.createCriteria().andEqualTo(condition);
        return orderDao.selectByExample(ex);
    }

    /**
     * 校验是否可以下单。
     * 1、租单信息：未接单
     * 2、押金余额：充足
     * 3、未完成订单数量不超过上限
     * 4、交易时间不重叠
     *
     * @param rentInfo  租单信息
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
        String userId = orderForm.getUserId();
        ResponseObj obj = super.isAllowed(userId, orderForm.getUserType());
        if (!obj.isSuccess()) {
            return obj;
        }

        DateTimeSection section = new DateTimeSection(rentInfo.getHandoverTime(), rentInfo.getGiveBackTime());
        //3. 未完成订单数量不超过上限
        List<OrderInfo> orderList = getOrderList(userId, orderForm.getUserType(), OrderStatus.HAND_OVER.getCode(), OrderStatus.PICK_UP.getCode(), OrderStatus.GIVE_BACK.getCode(), OrderStatus.ACCEPT.getCode());
        if (CollectionUtils.size(orderList) >= Constants.MAX_RENT_COUNT) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "最多发布" + Constants.MAX_RENT_COUNT + "笔租单");
        }

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
        if (!validateResult.isSuccess()) {
            return validateResult;
        }
        String userId = orderForm.getUserId();
        //用户信息
        RestResult<UserInfo> userResult = userService.getUserInfoById(userId);
        if (!userResult.isSuccess()) {
            log.warn("[REST]{}", userResult.getMessage());
            return ResponseObj.fail(StatusCode.BIZ_FAILED, userResult.getMessage());
        }
        UserInfo userInfo = userResult.getBody();
        //用户认证状态
        if (!UserStatus.AUTHENTICATED.getCode().equals(userInfo.getStatus())) {
            log.warn("[REST]{}", "该司机未认证或者已冻结");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该司机未认证或者已冻结");
        }
        //司机信息
        RestResult<DriverInfo> driverResult = userService.getDriverInfoByUserId(userInfo.getUserId());
        if (!driverResult.isSuccess()) {
            log.warn("[REST]{}", driverResult.getMessage());
            return ResponseObj.fail(StatusCode.BIZ_FAILED, driverResult.getMessage());
        }
        //租单信息不包含车辆，说明是司机发布的求租信息
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
        //订单ID复用租单的ID
        OrderInfo order = new OrderInfo(rentInfo.getRentId());
        buildDriverInfo(order, rentInfo, driverResult.getBody(), userInfo);
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
            //司机发单，车主接单
            order.setAgentUserId(rent.getUserId());
            order.setAgentDriverId(rent.getDriverId());
            order.setAgentDriverPhone(rent.getMobile());
            order.setAgentDriverName(rent.getDriverName());

            order.setOfficialUserId(user.getUserId());
            order.setOfficialDriverName(user.getName());
            order.setOfficialDriverId(driver.getId());
            order.setOfficialDriverPhone(driver.getMobile());
        } else if (rent.getUserType().equals(UserType.USER_APP_OFFICE.getCode())) {
            //车主发单，司机接单
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
     *
     * @param status 订单状态
     * @return OrderModel
     */
    private OrderModel getOrderModel(OrderStatus status) {
        if (status == null) {
            return null;
        }

        switch (status) {
            case HAND_OVER:
                return new HandoverOrderModelBase();
            case PICK_UP:
                return new PickUpOrderModel();
            case GIVE_BACK:
                return new GiveBackOrderModelBase();
            case ACCEPT:
                return new AcceptOrderModel();
            case CANCELED:
                return new CancelOrderModelBase();
            default:
                return null;
        }
    }
}
