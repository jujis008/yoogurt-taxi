package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderPickUpInfo;
import com.yoogurt.taxi.dal.model.order.PickUpOrderModel;
import com.yoogurt.taxi.order.form.PickUpForm;

public interface PickUpService extends OrderBizService {

	PickUpOrderModel doPickUp(PickUpForm pickupForm);

	OrderPickUpInfo getPickUpInfo(String orderId);
}
