package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.beans.OrderAcceptInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.order.AcceptOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.dao.AcceptDao;
import com.yoogurt.taxi.order.form.AcceptForm;
import com.yoogurt.taxi.order.form.OrderStatisticForm;
import com.yoogurt.taxi.order.service.AcceptService;
import com.yoogurt.taxi.order.service.CommonResourceService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service("acceptService")
public class AcceptServiceImpl extends AbstractOrderBizService implements AcceptService {

    @Autowired
    private AcceptDao acceptDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CommonResourceService resourceService;

    @Autowired
    private OrderStatisticService statisticService;

    @Transactional
    @Override
    public AcceptOrderModel doAccept(AcceptForm acceptForm) {
        Long orderId = acceptForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, acceptForm.getUserId());
        if(orderInfo == null) return null;
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待收车】
        if(!OrderStatus.ACCEPT.equals(status)) return null;
        OrderAcceptInfo acceptInfo = new OrderAcceptInfo();
        BeanUtils.copyProperties(acceptForm, acceptInfo);
        if (acceptDao.insertSelective(acceptInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            String[] pictures = acceptForm.getPictures();
            if (pictures != null && pictures.length > 0) {//添加图片资源
                List<CommonResource> resources = resourceService.assembleResources(orderId.toString(), "order_accept_info", pictures);
                resourceService.addResources(resources);
            }
            //统计订单数量-双方司机的订单数量各 +1
            statisticService.record(OrderStatisticForm.builder().userId(orderInfo.getOfficialUserId()).disobeyCount(1).build());
            statisticService.record(OrderStatisticForm.builder().userId(orderInfo.getAgentUserId()).disobeyCount(1).build());

            //订单已结束，通知代理司机
            super.push(orderInfo, UserType.USER_APP_AGENT, SendType.ORDER_FINISH, new HashMap<>());
            return (AcceptOrderModel) info(orderId, acceptForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderAcceptInfo getAcceptInfo(Long orderId) {
        return acceptDao.selectById(orderId);
    }

    @Override
    public OrderModel info(Long orderId, Long userId) {
        AcceptOrderModel model = new AcceptOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if(orderInfo == null) return null;
        BeanUtils.copyProperties(orderInfo, model);
        //下单时间
        model.setOrderTime(orderInfo.getGmtCreate());

        OrderAcceptInfo acceptInfo = getAcceptInfo(orderId);
        BeanUtils.copyProperties(acceptInfo, model);
        //实际收车时间
        model.setAcceptTime(acceptInfo.getGmtCreate());
        return model;
    }
}
