package com.yoogurt.taxi.user.dao.impl;

import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;
import com.yoogurt.taxi.user.dao.UserDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseDao<UserInfoMapper, UserInfo> implements UserDao {
}
