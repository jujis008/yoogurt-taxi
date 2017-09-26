package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.form.RentForm;

import java.util.List;

public interface RentInfoService {

	List<RentInfoModel> getRentList(RentPOICondition condition);

	Pager<RentInfoModel> getRentListByPage(RentListCondition condition);

	RentInfo getRentInfo(Long rentId);

	ResponseObj addRentInfo(RentForm rentForm);

}
