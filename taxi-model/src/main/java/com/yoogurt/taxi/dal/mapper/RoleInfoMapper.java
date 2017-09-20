package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleInfoMapper extends Mapper<RoleInfo> {
    List<RoleWLModel> getRoleWebList();
}