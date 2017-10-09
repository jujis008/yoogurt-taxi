package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.model.order.GiveBackOrderModel;
import com.yoogurt.taxi.order.form.GiveBackForm;

public interface GiveBackService extends OrderBizService {

	GiveBackOrderModel doGiveBack(GiveBackForm giveBackForm);

//	GiveBackOrderModel getGiveBackInfo(Long orderId);

}
