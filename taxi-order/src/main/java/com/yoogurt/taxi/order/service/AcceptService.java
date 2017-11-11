package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.OrderAcceptInfo;
import com.yoogurt.taxi.dal.model.order.AcceptOrderModel;
import com.yoogurt.taxi.order.form.AcceptForm;

public interface AcceptService extends OrderBizService {

	AcceptOrderModel doAccept(AcceptForm acceptForm);

	OrderAcceptInfo getAcceptInfo(String orderId);
}
