package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;

public interface GiveBackRuleService {

	String getIntroduction();

	OrderGiveBackRule getRuleInfo(int time, String unit);

	OrderGiveBackRule getRuleInfo();

    Money calculate(OrderGiveBackRule rule, int minutes);

}
