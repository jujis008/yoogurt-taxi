package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.condition.user.DriverWebListCondition;
import com.yoogurt.taxi.dal.model.user.DriverWebListModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DriverInfoMapper extends Mapper<DriverInfo> {
    Page<DriverWebListModel> getDriverWebList(DriverWebListCondition condition);

    int batchInsert(List<DriverInfo> list);
}