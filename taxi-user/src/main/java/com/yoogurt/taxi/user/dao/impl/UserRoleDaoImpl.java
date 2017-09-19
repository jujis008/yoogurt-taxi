package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.dal.mapper.UserRoleInfoMapper;
import com.yoogurt.taxi.user.dao.UserRoleDao;
import org.springframework.stereotype.Service;

@Service
public class UserRoleDaoImpl extends BaseDao<UserRoleInfoMapper, UserRoleInfo> implements UserRoleDao {
}
