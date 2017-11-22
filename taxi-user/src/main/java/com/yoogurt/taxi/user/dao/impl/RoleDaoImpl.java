package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.mapper.RoleInfoMapper;
import com.yoogurt.taxi.dal.model.user.RoleWebListModel;
import com.yoogurt.taxi.user.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDaoImpl extends BaseDao<RoleInfoMapper,RoleInfo> implements RoleDao{
    @Autowired
    private RoleInfoMapper roleInfoMapper;
    @Override
    public List<RoleWebListModel> getRoleWebList() {
        return roleInfoMapper.getRoleWebList();
    }
}
