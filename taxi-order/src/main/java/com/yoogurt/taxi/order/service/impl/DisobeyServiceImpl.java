package com.yoogurt.taxi.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.yoogurt.taxi.common.factory.PagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.dal.enums.DisobeyType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.dao.DisobeyDao;
import com.yoogurt.taxi.order.service.DisobeyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class DisobeyServiceImpl implements DisobeyService {

    @Autowired
    private DisobeyDao disobeyDao;

    @Autowired
    private PagerFactory appPagerFactory;

    @Autowired
    private PagerFactory webPagerFactory;

    /**
     * 获取违约记录
     *
     * @param condition 查询条件
     */
    @Override
    public Pager<OrderDisobeyInfo> getDisobeyList(DisobeyListCondition condition) {
        Example ex = buildExample(condition);
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize(), "happen_time DESC");
        Page<OrderDisobeyInfo> page = (Page<OrderDisobeyInfo>) disobeyDao.selectByExample(ex);
        if (condition.isFromApp()) {
            return appPagerFactory.generatePager(page);
        }
        return webPagerFactory.generatePager(page);
    }

    @Override
    public List<OrderDisobeyInfo> getDisobeyList(Long orderId, Long userId, DisobeyType... types) {
        List<OrderDisobeyInfo> disobeyInfoList = Lists.newArrayList();
        if(orderId == null && userId == null && (types == null || types.length == 0)) return disobeyInfoList;
        Example ex = new Example(OrderDisobeyInfo.class);
        Example.Criteria criteria = ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        if (orderId != null) {
            criteria.andEqualTo("orderId", orderId);
        }
        if (userId != null) {
            criteria.andEqualTo("userId", userId);
        }
        if (types != null && types.length > 0) {
            criteria.andIn("type", Arrays.asList(types));
        }
        return disobeyDao.selectByExample(ex);
    }

    @Override
    public OrderDisobeyInfo getDisobeyInfo(Long id) {
        if (id == null) return null;
        return disobeyDao.selectById(id);
    }

    @Override
    public OrderDisobeyInfo addDisobey(OrderDisobeyInfo disobey) {
        if (disobey == null) return null;
        if (disobeyDao.insertSelective(disobey) == 1) {

            return disobey;
        }
        return null;
    }

    /**
     * 构造一个违约记录
     *
     * @param orderInfo   订单信息
     * @param userType    用户类型
     * @param disobeyType 违约类型
     * @param ruleId      违约规则id
     * @param fineMoney   违约金
     * @param description 描述
     * @return 违约记录
     */
    @Override
    public OrderDisobeyInfo buildDisobeyInfo(OrderInfo orderInfo, UserType userType, DisobeyType disobeyType, Long ruleId, BigDecimal fineMoney, String description) {
        OrderDisobeyInfo disobey = new OrderDisobeyInfo(orderInfo.getOrderId());
        disobey.setRuleId(ruleId);
        disobey.setFineMoney(fineMoney);
        disobey.setHappenTime(new Date());
        disobey.setStatus(Boolean.FALSE);
        disobey.setUserType(userType != null ? userType.getCode() : 0);
        disobey.setType(disobeyType.getCode());
        disobey.setDescription(description);
        //注入司机信息
        driverInfo(orderInfo, disobey, userType);
        return disobey;
    }

    /**
     * 更改违约处理状态
     *
     * @param id     违约记录id
     * @param status 修改后的状态， true-已处理，false-未处理
     * @return 更改状态后的违约记录
     */
    @Override
    public OrderDisobeyInfo modifyStatus(Long id, boolean status) {
        OrderDisobeyInfo disobey = getDisobeyInfo(id);
        if (disobey == null) return null;
        disobey.setStatus(status);
        if (disobeyDao.updateByIdSelective(disobey) == 1) return disobey;
        return null;
    }

    private void driverInfo(OrderInfo orderInfo, OrderDisobeyInfo disobey, UserType userType) {
        disobey.setCarId(orderInfo.getCarId());
        disobey.setPlateNumber(orderInfo.getPlateNumber());
        disobey.setVin(orderInfo.getVin());
        if (userType.equals(UserType.USER_APP_OFFICE)) {
            disobey.setDriverId(orderInfo.getOfficialDriverId());
            disobey.setDriverName(orderInfo.getOfficialDriverPhone());
            disobey.setMobile(orderInfo.getOfficialDriverPhone());
        } else if (userType.equals(UserType.USER_APP_AGENT)) {
            disobey.setDriverId(orderInfo.getAgentDriverId());
            disobey.setDriverName(orderInfo.getAgentDriverPhone());
            disobey.setMobile(orderInfo.getAgentDriverPhone());
        }
    }

    private Example buildExample(DisobeyListCondition condition) {
        Example ex = new Example(OrderDisobeyInfo.class);
        Example.Criteria criteria = ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        if (condition.getOrderId() != null) {
            criteria.andEqualTo("orderId", condition.getOrderId());
        }

        if (condition.getUserId() != null) {
            criteria.andEqualTo("userId", condition.getUserId());
        }

        if (condition.getDriverId() != null) {
            criteria.andEqualTo("driverId", condition.getDriverId());
        }

        if (condition.getUserType() != null) {
            criteria.andEqualTo("userType", condition.getUserType());
        }

        if (condition.getDisobeyType() != null) {
            criteria.andEqualTo("type", condition.getDisobeyType());
        }

        if (StringUtils.isNoneBlank(condition.getDriverName())) {
            criteria.andEqualTo("driverName", condition.getDriverName());
        }

        if (StringUtils.isNoneBlank(condition.getMobile())) {
            criteria.andEqualTo("mobile", condition.getMobile());
        }

        if (condition.getStartTime() != null) {
            criteria.andGreaterThanOrEqualTo("happenTime", condition.getStartTime());
        }

        if (condition.getEndTime() != null) {
            criteria.andLessThanOrEqualTo("happenTime", condition.getEndTime());
        }
        return ex;
    }
}
