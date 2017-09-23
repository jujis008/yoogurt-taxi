package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.dal.model.user.DriverWLModel;
import com.yoogurt.taxi.user.dao.DriverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DriverDaoImpl extends BaseDao<DriverInfoMapper,DriverInfo> implements DriverDao{
    @Autowired
    private DriverInfoMapper driverInfoMapper;
    @Override
    public Page<DriverWLModel> getDriverWebList(DriverWLCondition condition) {
        return driverInfoMapper.getDriverWebList(condition);
    }

    @Override
    public int batchInsert(List<DriverInfo> list) {
        return driverInfoMapper.batchInsert(list);
    }
}
