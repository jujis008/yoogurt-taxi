package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWebListCondition;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;
import com.yoogurt.taxi.dal.model.user.UserWebListModel;
import com.yoogurt.taxi.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl extends BaseDao<UserInfoMapper,UserInfo> implements UserDao {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Page<UserWebListModel> getUserWebListPage(UserWebListCondition condition) {
        return userInfoMapper.getUserWebListPage(condition);
    }

    @Override
    public int batchInsert(List<UserInfo> list) {
        return userInfoMapper.batchInsert(list);
    }

}
