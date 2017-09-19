package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.user.dao.DriverDao;
import org.springframework.stereotype.Repository;

@Repository
public class DriverDaoImpl extends BaseDao<DriverInfoMapper,DriverInfo> implements DriverDao{
}
