package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentPOIModel;
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
    public List<RentPOIModel> getRentList(Date startTime, Date endTime, String keywords) {
        return rentInfoMapper.getRentList(startTime, endTime, keywords);
    }
}

