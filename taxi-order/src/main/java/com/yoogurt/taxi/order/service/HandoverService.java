package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderHandoverInfo;
import com.yoogurt.taxi.dal.model.order.HandoverOrderModel;
import com.yoogurt.taxi.order.form.HandoverForm;

public interface HandoverService extends OrderBizService {

	/**
	 * 车主确认交车
	 */
	HandoverOrderModel doHandover(HandoverForm handoverForm);

//	HandoverOrderModel getHandoverInfo(Long orderId);

	OrderHandoverInfo getHandoverInfo(Long orderId);
}
