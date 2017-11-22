package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPickUpInfo;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.model.order.PickUpOrderModel;
import com.yoogurt.taxi.order.dao.PickUpDao;
import com.yoogurt.taxi.order.form.PickUpForm;
import com.yoogurt.taxi.order.service.CommonResourceService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.PickUpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service("pickUpService")
public class PickUpServiceImpl implements PickUpService {

    @Autowired
    private PickUpDao pickUpDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CommonResourceService resourceService;

    @Autowired
    private RedisHelper redisHelper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PickUpOrderModel doPickUp(PickUpForm pickupForm) {
        String orderId = pickupForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, pickupForm.getUserId());
        if (orderInfo == null) {
            return null;
        }
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待取车】
        if (!OrderStatus.PICK_UP.equals(status)) {
            return null;
        }
        OrderPickUpInfo pickUpInfo = new OrderPickUpInfo();
        BeanUtils.copyProperties(pickupForm, pickUpInfo);
        if (pickUpDao.insertSelective(pickUpInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime giveBackTime = LocalDateTime.ofInstant(orderInfo.getGiveBackTime().toInstant(), ZoneId.systemDefault());
            long durationSeconds =  Duration.between(now, giveBackTime).getSeconds();
            //剩余时间不足1小时，不需要添加任务
            if (durationSeconds - 3600 > 10) {
                //设置还车1小时前提醒任务
                redisHelper.set(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER1_KEY+ orderId, orderId, durationSeconds - 3600);
            }
            //设置还车到点提醒任务
            redisHelper.set(CacheKey.MESSAGE_ORDER_GIVE_BACK_REMINDER_KEY + orderId, orderId, durationSeconds);

            String[] pictures = pickupForm.getPictures();
            //添加图片资源
            if (pictures != null && pictures.length > 0) {
                List<CommonResource> resources = resourceService.assembleResources(orderId.toString(), "order_pick_up_info", pictures);
                resourceService.addResources(resources);
            }
            return (PickUpOrderModel) info(orderId, pickupForm.getUserId());
        }

        return null;
    }

    @Override
    public OrderPickUpInfo getPickUpInfo(String orderId) {
        return pickUpDao.selectById(orderId);
    }

    @Override
    public OrderModel info(String orderId, String userId) {

        PickUpOrderModel model = new PickUpOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) {
            return null;
        }
        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderPickUpInfo pickUpInfo = getPickUpInfo(orderId);
        BeanUtils.copyProperties(pickUpInfo, model);
        model.setPickUpTime(pickUpInfo.getGmtCreate());

        return model;
    }
}
