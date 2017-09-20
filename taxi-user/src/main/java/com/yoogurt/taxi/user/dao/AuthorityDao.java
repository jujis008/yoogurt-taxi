package com.yoogurt.taxi.user.dao;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.mapper.AuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;

import java.util.List;

public interface AuthorityDao extends IDao<AuthorityInfoMapper,AuthorityInfo>{
    Page<AuthorityWLModel> getAuthorityWebList(AuthorityWLCondition condition);
    List<GroupAuthorityLModel> getAllAuthorities();
}
