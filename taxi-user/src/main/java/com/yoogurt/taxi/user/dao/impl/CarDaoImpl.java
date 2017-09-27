package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.mapper.CarInfoMapper;
import com.yoogurt.taxi.user.dao.CarDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarDaoImpl extends BaseDao<CarInfoMapper,CarInfo> implements CarDao{
    @Autowired
    private CarInfoMapper carInfoMapper;
    @Override
    public int batchInsert(List<CarInfo> carInfoList) {
        return carInfoMapper.batchInsert(carInfoList);
    }
}
