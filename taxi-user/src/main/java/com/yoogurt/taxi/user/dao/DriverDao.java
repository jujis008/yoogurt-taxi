package com.yoogurt.taxi.user.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;
import com.yoogurt.taxi.dal.mapper.DriverInfoMapper;
import com.yoogurt.taxi.dal.model.user.DriverWebListModel;

import java.util.List;

public interface DriverDao extends IDao<DriverInfoMapper,DriverInfo> {
    Page<DriverWebListModel> getDriverWebList(DriverWebListCondition condition);
    int batchInsert(List<DriverInfo> list);
    List<DriverInfo> getDriverByUserId(String userId);
}
