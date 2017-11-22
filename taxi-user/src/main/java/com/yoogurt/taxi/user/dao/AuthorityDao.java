package com.yoogurt.taxi.user.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWebListCondition;
import com.yoogurt.taxi.dal.mapper.AuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.user.AuthorityWebListModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityListModel;

import java.util.List;

public interface AuthorityDao extends IDao<AuthorityInfoMapper,AuthorityInfo>{
    Page<AuthorityWebListModel> getAuthorityWebList(AuthorityWebListCondition condition);
    List<GroupAuthorityListModel> getAllAuthorities();
    List<String> getAssociatedControlByUserId(String userId);
}
