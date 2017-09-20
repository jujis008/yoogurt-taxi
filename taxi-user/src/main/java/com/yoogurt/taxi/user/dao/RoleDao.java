package com.yoogurt.taxi.user.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.mapper.RoleInfoMapper;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;

import java.util.List;

public interface RoleDao extends IDao<RoleInfoMapper,RoleInfo>{
    List<RoleWLModel> getRoleWebList();
}
