package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
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
public class UserDaoImpl extends BaseDao<UserInfoMapper,UserInfo> implements UserDao {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Page<UserWLModel> getUserWebListPage(UserWLCondition condition) {
        return userInfoMapper.getUserWebListPage(condition);
    }

    @Override
    public int insertUsers(List<UserInfo> list) {
        return userInfoMapper.insertUsers(list);
    }

}
