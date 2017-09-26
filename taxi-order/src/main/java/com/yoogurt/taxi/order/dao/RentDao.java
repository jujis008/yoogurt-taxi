package com.yoogurt.taxi.order.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;

import java.util.Date;
import java.util.List;

public interface RentDao extends IDao<RentInfoMapper, RentInfo> {

    List<RentInfoModel> getRentList(Double maxLng, Double minLng, Double maxLat, Double minLat, Date startTime, Date endTime, String keywords);

    Page<RentInfoModel> getRentListByPage(Double maxLng, Double minLng, Double maxLat, Double minLat, Date startTime, Date endTime, String keywords, String sortName, String sortOrder);
}
