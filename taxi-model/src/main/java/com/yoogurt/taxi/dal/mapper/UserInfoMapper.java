package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.model.user.UserWLModel;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface UserInfoMapper extends Mapper<UserInfo> {
    Page<UserWLModel> getUserWebListPage(UserWLCondition condition);
    int batchInsert(List<UserInfo> list);
}