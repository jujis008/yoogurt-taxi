package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.model.order.RentPOIModel;
import com.yoogurt.taxi.order.form.RentForm;

import java.util.List;

public interface RentInfoService {

	List<RentPOIModel> getRentList(RentPOICondition condition);

	RentInfo getRentInfo(Long rentId);

	RentInfo addRentInfo(RentForm rentForm);

	RentInfo buildRentInfo(RentForm rentForm);
}
