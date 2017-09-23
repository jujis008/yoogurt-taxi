package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentPOIModel;

import java.util.Date;
import java.util.List;

public interface RentDao extends IDao<RentInfoMapper, RentInfo> {

    List<RentPOIModel> getRentList(Date startTime, Date endTime, String keywords);
}