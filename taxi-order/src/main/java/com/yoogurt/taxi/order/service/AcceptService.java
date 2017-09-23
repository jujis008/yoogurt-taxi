package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.model.order.AcceptOrderModel;
import com.yoogurt.taxi.order.form.AcceptForm;

public interface AcceptService {

	AcceptOrderModel doAccept(AcceptForm acceptForm);

	AcceptOrderModel getAcceptInfo(Long orderId);

}
