package com.yoogurt.taxi.user.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.user.UserWebListCondition;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;
import com.yoogurt.taxi.dal.model.user.UserWebListModel;

import java.util.List;

/**
 * Description:
 * 用户数据访问层
 * @author Eric Lau
 * @Date 2017/8/28.
 */
public interface UserDao extends IDao<UserInfoMapper, UserInfo> {


    Page<UserWebListModel> getUserWebListPage(UserWebListCondition condition);

    int batchInsert(List<UserInfo> list);

}
