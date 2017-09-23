package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.model.order.PickUpOrderModel;
import com.yoogurt.taxi.order.form.PickUpForm;

public interface PickUpService {

	PickUpOrderModel doPickUp(PickUpForm pickupForm);

	PickUpOrderModel getPickUpInfo(Long orderId);

}
