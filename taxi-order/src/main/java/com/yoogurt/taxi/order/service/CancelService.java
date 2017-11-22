package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.OrderCancelInfo;
import com.yoogurt.taxi.dal.model.order.CancelOrderModelBase;
import com.yoogurt.taxi.order.form.CancelForm;

public interface CancelService extends OrderBizService {

	CancelOrderModelBase doCancel(CancelForm cancelForm);

	OrderCancelInfo getCancelInfo(String orderId);

}
