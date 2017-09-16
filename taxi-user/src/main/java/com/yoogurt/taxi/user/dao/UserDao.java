package com.yoogurt.taxi.user.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.mapper.UserInfoMapper;

/**
 * Description:
 * 用户数据访问层
 * @author Eric Lau
 * @Date 2017/8/28.
 */
public interface UserDao extends IDao<UserInfoMapper, UserInfo>{
}
