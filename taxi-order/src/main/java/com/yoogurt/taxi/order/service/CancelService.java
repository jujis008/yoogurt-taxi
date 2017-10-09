package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.order.form.CancelForm;

public interface CancelService extends OrderBizService {

	CancelOrderModel doCancel(CancelForm cancelForm);

//	CancelOrderModel getCancelInfo(Long orderId);

}
