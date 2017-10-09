package com.yoogurt.taxi.order.service.impl;

import com.google.common.collect.Lists;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPickUpInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.model.order.PickUpOrderModel;
import com.yoogurt.taxi.order.dao.PickUpDao;
import com.yoogurt.taxi.order.form.PickUpForm;
import com.yoogurt.taxi.order.service.CommonResourceService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.PickUpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("pickUpService")
public class PickUpServiceImpl implements PickUpService {

    @Autowired
    private PickUpDao pickUpDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CommonResourceService resourceService;

    @Transactional
    @Override
    public PickUpOrderModel doPickUp(PickUpForm pickupForm) {
        Long orderId = pickupForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待取车】
        if(!OrderStatus.PICK_UP.equals(status)) return null;
        OrderPickUpInfo pickUpInfo = new OrderPickUpInfo();
        BeanUtils.copyProperties(pickupForm, pickUpInfo);
        if (pickUpDao.insertSelective(pickUpInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            String[] pictures = pickupForm.getPictures();
            if (pictures.length > 1) {//添加图片资源
                List<CommonResource> resources = assembleResources(pickUpInfo.getOrderId().toString(), pictures);
                resourceService.addResources(resources);
            }
            return (PickUpOrderModel) info(orderId);
        }

        return null;
    }

    @Override
    public OrderPickUpInfo getPickUpInfo(Long orderId) {
        return pickUpDao.selectById(orderId);
    }

    @Override
    public OrderModel info(Long orderId) {

        PickUpOrderModel model = new PickUpOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderPickUpInfo pickUpInfo = getPickUpInfo(orderId);
        BeanUtils.copyProperties(pickUpInfo, model);
        model.setPickUpTime(pickUpInfo.getGmtCreate());

        return model;
    }

    private List<CommonResource> assembleResources(String linkId, String[] pictures) {
        List<CommonResource> resources = Lists.newArrayList();
        if(StringUtils.isBlank(linkId) || pictures == null || pictures.length == 0) return resources;
        for (String picture : pictures) {
            CommonResource resource = new CommonResource();
            resource.setLinkId(linkId);
            resource.setTableName("order_pick_up_info");
            resource.setUrl(picture);
            resource.setResourceName(picture.substring(picture.lastIndexOf("/") + 1));
            resources.add(resource);
        }
        return resources;
    }
}
