package com.yoogurt.taxi.user.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.mapper.CarInfoMapper;

import java.util.List;

public interface CarDao extends IDao<CarInfoMapper,CarInfo>{
    int insertCars(List<CarInfo> carInfoList);
}
