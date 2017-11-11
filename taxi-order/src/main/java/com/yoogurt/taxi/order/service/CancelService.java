package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.order.form.CancelForm;

import java.util.List;

public interface CancelService extends OrderBizService {

	CancelOrderModel doCancel(CancelForm cancelForm);

	OrderCancelInfo getCancelInfo(String orderId);

}
