package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.CarInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CarInfoMapper extends Mapper<CarInfo> {
    int batchInsert(List<CarInfo> carInfoList);
}