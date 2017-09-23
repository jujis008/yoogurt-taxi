package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.model.order.RentPOIModel;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.Date;
import java.util.List;

public interface RentInfoMapper extends Mapper<RentInfo>, MySqlMapper<RentInfo> {

    List<RentPOIModel> getRentList(@Param("startTime")Date startTime, @Param("endTime") Date endTime, @Param("keywords") String keywords);

}