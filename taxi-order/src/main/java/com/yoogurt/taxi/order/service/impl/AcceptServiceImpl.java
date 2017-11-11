package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.beans.OrderAcceptInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.order.AcceptOrderModel;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.dal.vo.ModificationVo;
import com.yoogurt.taxi.order.dao.AcceptDao;
import com.yoogurt.taxi.order.form.AcceptForm;
import com.yoogurt.taxi.order.form.OrderStatisticForm;
import com.yoogurt.taxi.order.service.*;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import org.apache.commons.collections.CollectionUtils;
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
    private OrderPaymentService paymentService;

    @Autowired
    private RestAccountService accountService;

    @Autowired
    private CommonResourceService resourceService;

    @Autowired
    private OrderStatisticService statisticService;

    @Transactional
    @Override
    public AcceptOrderModel doAccept(AcceptForm acceptForm) {
        String orderId = acceptForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, acceptForm.getUserId());
        if (orderInfo == null) return null;
        OrderStatus status = OrderStatus.getEnumsByCode(orderInfo.getStatus());
        //订单状态不是 【待收车】
        if (!OrderStatus.ACCEPT.equals(status)) return null;
        OrderAcceptInfo acceptInfo = new OrderAcceptInfo();
        BeanUtils.copyProperties(acceptForm, acceptInfo);
        //插入收车记录信息
        if (acceptDao.insertSelective(acceptInfo) == 1) {
            //修改订单状态
            orderInfoService.modifyStatus(orderId, status.next());
            //获取司机的支付信息
            if (orderInfo.getIsPaid()) {
                ModificationVo vo = ModificationVo.builder().contextId(orderInfo.getOrderId())
                        .userId(orderInfo.getOfficialUserId())
                        .outUserId(orderInfo.getAgentUserId())
                        .inUserId(orderInfo.getOfficialUserId())
                        .money(orderInfo.getAmount())
                        .type(TradeType.INCOME.getCode()).build();
                List<OrderPayment> payments = paymentService.getPayments(orderId, 20);
                if (CollectionUtils.isNotEmpty(payments)) {
                    OrderPayment payment = payments.get(0);
                    PayChannel channel = PayChannel.getChannelByName(payment.getPayChannel());
                    if (channel != null) {
                        if (channel.isWx()) {
                            vo.setPayment(Payment.WEIXIN.getCode());
                        } else if (channel.isAlipay()) {
                            vo.setPayment(Payment.ALIPAY.getCode());
                        } else {
                            vo.setPayment(Payment.BANK.getCode());
                        }
                    }
                } else {
                    vo.setPayment(Payment.BALANCE.getCode());
                }
                accountService.updateAccount(vo);
            }
            //添加图片资源
            String[] pictures = acceptForm.getPictures();
            if (pictures != null && pictures.length > 0) {
                List<CommonResource> resources = resourceService.assembleResources(orderId, "order_accept_info", pictures);
                resourceService.addResources(resources);
            }
            //统计订单数量-双方司机的订单数量各 +1
            statisticService.record(OrderStatisticForm.builder().userId(orderInfo.getOfficialUserId()).orderCount(1).build());
            statisticService.record(OrderStatisticForm.builder().userId(orderInfo.getAgentUserId()).orderCount(1).build());

            //订单已结束，通知司机
            super.push(orderInfo, UserType.USER_APP_AGENT, SendType.ORDER_FINISH, new HashMap<>());
            return (AcceptOrderModel) info(orderId, acceptForm.getUserId());
        }
        return null;
    }

    @Override
    public OrderAcceptInfo getAcceptInfo(String orderId) {
        return acceptDao.selectById(orderId);
    }

    @Override
    public OrderModel info(String orderId, String userId) {
        AcceptOrderModel model = new AcceptOrderModel();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, userId);
        if (orderInfo == null) return null;
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
