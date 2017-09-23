package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.model.order.HandoverOrderModel;
import com.yoogurt.taxi.order.form.HandoverForm;

public interface HandoverService {

	/**
	 * 正式司机确认交车
	 */
	HandoverOrderModel doHandover(HandoverForm handoverForm);

	HandoverOrderModel getHandoverInfo(Long orderId);

}
