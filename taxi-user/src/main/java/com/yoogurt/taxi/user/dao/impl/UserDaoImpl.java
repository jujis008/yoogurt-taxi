package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.impl.BatchDao;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import com.yoogurt.taxi.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl extends BatchDao<UserInfoMapper, UserInfo> implements UserDao {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Page<UserWLModel> getUserWebListPage(UserWLCondition condition) {
        return userInfoMapper.getUserWebListPage(condition);
    }

}
