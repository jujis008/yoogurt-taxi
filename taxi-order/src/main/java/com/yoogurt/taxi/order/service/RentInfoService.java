package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;

import java.util.List;

public interface RentInfoService {

	List<RentInfo> getRentList(RentListCondition condition);

	RentInfo getRentInfo(Long rentId);

}
