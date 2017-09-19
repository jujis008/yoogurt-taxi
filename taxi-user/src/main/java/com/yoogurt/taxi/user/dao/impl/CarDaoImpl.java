package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.mapper.CarInfoMapper;
import com.yoogurt.taxi.user.dao.CarDao;
import org.springframework.stereotype.Repository;

@Repository
public class CarDaoImpl extends BaseDao<CarInfoMapper,CarInfo> implements CarDao{
}
