package com.yoogurt.taxi.user.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.dal.model.user.DriverWLModel;

public interface DriverDao extends IDao<DriverInfoMapper,DriverInfo>{
    Page<DriverWLModel> getDriverWebList(DriverWLCondition condition);
}
