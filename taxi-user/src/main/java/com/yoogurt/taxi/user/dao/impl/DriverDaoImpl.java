package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IBatchDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.common.dao.impl.BatchDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.dal.model.user.DriverWLModel;
import com.yoogurt.taxi.user.dao.DriverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

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
    public int insertDrivers(List<DriverInfo> list) {
        return driverInfoMapper.insertDrivers(list);
    }

    @Override
    public List<DriverInfo> getDriverByUserId(String userId) {
        Example example = new Example(DriverInfo.class);
        example.createCriteria().andEqualTo("userId",userId);
        return driverInfoMapper.selectByExample(example);
    }
}
