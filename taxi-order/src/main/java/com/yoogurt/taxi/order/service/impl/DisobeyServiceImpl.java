package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.dal.enums.DisobeyType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.dao.DisobeyDao;
import com.yoogurt.taxi.order.service.DisobeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class DisobeyServiceImpl implements DisobeyService {

    @Autowired
    private DisobeyDao disobeyDao;

    /**
     * 获取违约记录
     *
     * @param condition 查询条件
     */
    @Override
    public List<OrderDisobeyInfo> getDisobeyList(DisobeyListCondition condition) {
        return null;
    }

    @Override
    public OrderDisobeyInfo getDisobeyInfo(Long id) {
        if(id == null) return null;
        return disobeyDao.selectById(id);
    }

    @Override
    public OrderDisobeyInfo addDisobey(OrderDisobeyInfo disobey) {
        if(disobey == null) return null;
        if(disobeyDao.insertSelective(disobey) == 1){

            return disobey;
        }
        return null;
    }

    /**
     * 构造一个违约记录
     * @param orderInfo 订单信息
     * @param userType 用户类型
     * @param disobeyType 违约类型
     * @param ruleId 违约规则id
     * @param fineMoney 违约金
     * @param description 描述
     * @return 违约记录
     */
    @Override
    public OrderDisobeyInfo buildDisobeyInfo(OrderInfo orderInfo, UserType userType, DisobeyType disobeyType, Long ruleId, BigDecimal fineMoney, String description) {
        OrderDisobeyInfo disobey = new OrderDisobeyInfo(orderInfo.getOrderId());
        disobey.setRuleId(ruleId);
        disobey.setType(userType.getCode());
        disobey.setStatus(Boolean.FALSE);
        disobey.setFineMoney(fineMoney);
        disobey.setHappenTime(new Date());
        disobey.setCarId(orderInfo.getCarId());
        disobey.setType(disobeyType.getCode());
        disobey.setUserType(userType.getCode());
        disobey.setPlateNumber(orderInfo.getPlateNumber());
        disobey.setVin(orderInfo.getVin());
        disobey.setDescription(description);
        //注入司机信息
        driverInfo(orderInfo, disobey, userType);
        return disobey;
    }

    /**
     * 更改违约处理状态
     *
     * @param id 违约记录id
     * @param status 修改后的状态， true-已处理，false-未处理
     * @return 更改状态后的违约记录
     */
    @Override
    public OrderDisobeyInfo modifyStatus(Long id, boolean status) {
        OrderDisobeyInfo disobey = getDisobeyInfo(id);
        if(disobey == null) return null;
        disobey.setStatus(status);
        if(disobeyDao.updateByIdSelective(disobey) == 1) return disobey;
        return null;
    }

    private void driverInfo(OrderInfo orderInfo, OrderDisobeyInfo disobey, UserType userType) {
        if(userType.equals(UserType.USER_APP_OFFICE)) {
            disobey.setDriverId(orderInfo.getOfficialDriverId());
            disobey.setDriverName(orderInfo.getOfficialDriverPhone());
            disobey.setMobile(orderInfo.getOfficialDriverPhone());
        } else if (userType.equals(UserType.USER_APP_AGENT)) {
            disobey.setDriverId(orderInfo.getAgentDriverId());
            disobey.setDriverName(orderInfo.getAgentDriverPhone());
            disobey.setMobile(orderInfo.getAgentDriverPhone());
        }
    }
}
