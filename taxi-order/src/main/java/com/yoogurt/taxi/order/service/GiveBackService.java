package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderGiveBackInfo;
import com.yoogurt.taxi.dal.model.order.GiveBackOrderModelBase;
import com.yoogurt.taxi.order.form.GiveBackForm;

public interface GiveBackService extends OrderBizService {

	GiveBackOrderModelBase doGiveBack(GiveBackForm giveBackForm);

	OrderGiveBackInfo getGiveBackInfo(String orderId);
}
