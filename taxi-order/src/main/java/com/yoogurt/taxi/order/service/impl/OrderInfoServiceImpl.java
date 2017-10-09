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
import com.yoogurt.taxi.dal.model.order.*;
import com.yoogurt.taxi.order.dao.OrderDao;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.OrderBizService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

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
    private PagerFactory appPagerFactory;

    @Autowired
    private PagerFactory webPagerFactory;


    @Override
    public ResponseObj placeOrder(PlaceOrderForm orderForm) {
        ResponseObj obj = buildOrderInfo(orderForm);
        if (obj.isSuccess() && orderDao.insertSelective((OrderInfo) obj.getBody()) == 1) {
            //更改租单状态 --> 已接单
            rentInfoService.modifyStatus(orderForm.getRentId(), RentStatus.RENT);
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(obj.getBody(), model);
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
    public OrderInfo getOrderInfo(Long orderId) {
        return orderDao.selectById(orderId);
    }


    @Override
    public OrderModel info(Long orderId) {
        OrderModel model = new OrderModel();
        OrderInfo orderInfo = getOrderInfo(orderId);
        if (orderInfo != null) {
            BeanUtils.copyProperties(orderInfo, model);
            model.setOrderTime(orderInfo.getGmtCreate());
        }
        return model;
    }

    /**
     * 订单详情接口
     *
     * @param orderId 订单id
     * @return 订单详细信息
     */
    @Override
    public OrderModel getOrderDetails(Long orderId) {
        OrderInfo orderInfo = getOrderInfo(orderId);
        if (orderInfo == null) return null;
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //待交车的订单，只有主订单信息
        if (status.equals(OrderStatus.HAND_OVER)) {
            OrderModel model = new OrderModel();
            BeanUtils.copyProperties(orderInfo, model);
            model.setOrderTime(orderInfo.getGmtCreate());
            return model;
        }
        //根据订单状态生成对应的model，此时对象的属性还未注入
        OrderModel model = getOrderModel(status);
        //将主订单信息拷贝到model中
        BeanUtils.copyProperties(orderInfo, model);
        //根据名称，获取service
        //这里需要子订单的各个service继承OrderBizService接口
        OrderBizService service = (OrderBizService) context.getBean(model.getServiceName());
        //此步骤是获取子订单信息
        OrderModel m = service.info(orderId);
        //将子订单信息拷贝到model中
        BeanUtils.copyProperties(m, model);
        return model;
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
        OrderInfo orderInfo = getOrderInfo(orderId);
        if(orderInfo == null) return false;
        if(status.getCode().equals(orderInfo.getStatus())) return true; //与原租单状态相同，直接返回true
        if(status.getCode() < orderInfo.getStatus()) return false; //不允许状态回退

        orderInfo.setStatus(status.getCode());
        return orderDao.updateByIdSelective(orderInfo) == 1;
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
        if (!RentStatus.WAITING.getCode().equals(rentInfo.getStatus())) {
            log.warn("该租单信息已被他人接单");
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "该租单信息已被他人接单");
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
        ResponseObj validateResult = isAllowOrder(rentInfo, orderForm);
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
                return new HandoverOrderModel();
            case GIVE_BACK:
                return new PickUpOrderModel();
            case ACCEPT:
                return new GiveBackOrderModel();
            case FINISH:
                return new AcceptOrderModel();
            case CANCELED:
                return new CancelOrderModel();
            default:
                return null;
        }
    }
}
