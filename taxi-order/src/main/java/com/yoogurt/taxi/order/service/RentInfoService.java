package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.dal.condition.order.RentWebListCondition;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.form.RentCancelForm;
import com.yoogurt.taxi.order.form.RentForm;

import java.util.List;

public interface RentInfoService {

	List<RentInfoModel> getRentList(RentPOICondition condition);

	Pager<RentInfoModel> getRentListByPage(RentListCondition condition);

	List<RentInfo> getRentInfoList(Long userId, Integer pageNum, Integer pageSize, Integer... status);

	List<RentInfo> getRentList(Long userId, Integer... status);

	RentInfo getRentInfo(Long rentId, Long userId);

	ResponseObj addRentInfo(RentForm rentForm);

	RentInfo cancel(RentCancelForm cancelForm);

	boolean modifyStatus(Long rentId, RentStatus status);

	Pager<RentInfo> getRentListForWebPage(RentWebListCondition condition);
}
