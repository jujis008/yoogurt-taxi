package com.yoogurt.taxi.order.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.dao.RentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class RentDaoImpl extends BaseDao<RentInfoMapper, RentInfo> implements RentDao {

    @Autowired
    private RentInfoMapper rentInfoMapper;

    @Override
    public List<RentInfoModel> getRentList(Double maxLng, Double minLng, Double maxLat, Double minLat, Date startTime, Date endTime, String keywords) {
        return rentInfoMapper.getRentList(maxLng, minLng, maxLat, minLat, startTime, endTime, keywords);
    }

    @Override
    public Page<RentInfoModel> getRentListByPage(Double maxLng, Double minLng, Double maxLat, Double minLat, Date startTime, Date endTime, String keywords, String sortName, String sortOrder) {
        return rentInfoMapper.getRentListByPage(maxLng, minLng, maxLat, minLat, startTime, endTime, keywords, sortName, sortOrder);
    }
}

