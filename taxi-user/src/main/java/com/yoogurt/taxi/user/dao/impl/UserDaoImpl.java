package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.BaseDao;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends BaseDao<UserInfoMapper, UserInfo> implements UserDao {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Page<UserWLModel> getUserWebListPage(UserWLCondition condition) {
        return userInfoMapper.getUserWebListPage(condition);
    }
}
