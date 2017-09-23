package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderDisobeyInfo;
import com.yoogurt.taxi.dal.condition.order.DisobeyListCondition;
import com.yoogurt.taxi.order.form.DisobeyForm;

import java.math.BigDecimal;
import java.util.List;

public interface DisobeyService {

	/**
	 * 获取违约记录
	 */
	List<OrderDisobeyInfo> getDisobeyList(DisobeyListCondition condition);

	OrderDisobeyInfo getDisobeyInfo(Long id);

	OrderDisobeyInfo addDisobey(OrderDisobeyInfo disobey);

	/**
	 * 构造一个违约记录
	 */
	OrderDisobeyInfo buildDisobeyInfo(DisobeyForm disobeyForm);

	/**
	 * 计算应付违约金
	 */
	BigDecimal getFineMoney(DisobeyForm disobeyForm);

	/**
	 * 更改违约处理状态
	 */
	Boolean modifyStatus(int status);

}
