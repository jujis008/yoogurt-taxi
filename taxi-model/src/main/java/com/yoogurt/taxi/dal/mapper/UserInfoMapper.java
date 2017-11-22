package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.mapper.SuperMapper;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWebListCondition;
import com.yoogurt.taxi.dal.model.user.UserWebListModel;

import java.util.List;

public interface UserInfoMapper extends SuperMapper<UserInfo> {
    Page<UserWebListModel> getUserWebListPage(UserWebListCondition condition);
    int batchInsert(List<UserInfo> list);
}