package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;

public interface GiveBackRuleService {

	String getIntroduction();

	OrderGiveBackRule getRuleInfo(int time);

}
