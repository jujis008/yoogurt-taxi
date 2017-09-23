package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.RentInfo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RentInfoMapper extends Mapper<RentInfo>, MySqlMapper<RentInfo> {
}