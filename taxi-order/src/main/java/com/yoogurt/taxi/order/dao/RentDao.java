package com.yoogurt.taxi.order.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPoiCondition;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;

import java.util.List;

public interface RentDao extends IDao<RentInfoMapper, RentInfo> {

    List<RentInfoModel> getRentList(RentPoiCondition condition);

    Page<RentInfoModel> getRentListByPage(RentListCondition condition);
}
