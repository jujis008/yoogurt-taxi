package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.dal.enums.DisobeyType;
import com.yoogurt.taxi.dal.enums.UserType;

import java.math.BigDecimal;
import java.util.List;

public interface DisobeyService {

	/**
	 * 获取违约记录
	 */
	Pager<OrderDisobeyInfo> getDisobeyList(DisobeyListCondition condition);

	List<OrderDisobeyInfo> getDisobeyList(Long orderId, Long driverId, DisobeyType... types);

	OrderDisobeyInfo getDisobeyInfo(Long id);

	OrderDisobeyInfo addDisobey(OrderDisobeyInfo disobey);

	/**
	 * 构造一个违约记录
	 */
	OrderDisobeyInfo buildDisobeyInfo(OrderInfo orderInfo, UserType userType, DisobeyType disobeyType, Long ruleId, BigDecimal fineMoney, String description);


	/**
	 * 更改违约处理状态
	 */
	OrderDisobeyInfo modifyStatus(Long id, boolean status);

}
