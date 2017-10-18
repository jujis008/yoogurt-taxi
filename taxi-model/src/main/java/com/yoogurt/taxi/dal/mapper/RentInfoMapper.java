package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.Date;
import java.util.List;

public interface RentInfoMapper extends Mapper<RentInfo>, MySqlMapper<RentInfo> {

    List<RentInfoModel> getRentList(
            @Param("userId") Long userId, @Param("userType") Integer userType, @Param("status") Integer status,
            @Param("maxLng") Double maxLng, @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat, @Param("minLat") Double minLat,
            @Param("startTime") Date startTime, @Param("endTime") Date endTime,
            @Param("keywords") String keywords);

    Page<RentInfoModel> getRentListByPage(
            @Param("userId") Long userId, @Param("userType") Integer userType, @Param("status") Integer status,
            @Param("maxLng") Double maxLng, @Param("minLng") Double minLng,
            @Param("maxLat") Double maxLat, @Param("minLat") Double minLat,
            @Param("startTime") Date startTime, @Param("endTime") Date endTime,
            @Param("keywords") String keywords, @Param("sortName") String sortName, @Param("sortOrder") String sortOrder);
}