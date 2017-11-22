package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderHandoverInfo;
import com.yoogurt.taxi.dal.model.order.HandoverOrderModelBase;
import com.yoogurt.taxi.order.form.HandoverForm;

public interface HandoverService extends OrderBizService {

	/**
	 * 车主确认交车
	 */
	HandoverOrderModelBase doHandover(HandoverForm handoverForm);

	OrderHandoverInfo getHandoverInfo(String orderId);
}
