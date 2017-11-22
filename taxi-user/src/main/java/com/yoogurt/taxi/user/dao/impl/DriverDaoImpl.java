package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.dal.model.user.DriverWebListModel;
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
    public Page<DriverWebListModel> getDriverWebList(DriverWebListCondition condition) {
        return driverInfoMapper.getDriverWebList(condition);
    }

    @Override
    public int batchInsert(List<DriverInfo> list) {
        return driverInfoMapper.batchInsert(list);
    }

    @Override
    public List<DriverInfo> getDriverByUserId(String userId) {
        Example example = new Example(DriverInfo.class);
        example.createCriteria().andEqualTo("userId",userId);
        return driverInfoMapper.selectByExample(example);
    }
}
