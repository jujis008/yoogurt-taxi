package com.yoogurt.taxi.user.dao.impl;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.mapper.AuthorityInfoMapper;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.user.dao.AuthorityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorityDaoImpl extends BaseDao<AuthorityInfoMapper,AuthorityInfo> implements AuthorityDao{
    @Autowired
    private AuthorityInfoMapper authorityInfoMapper;

    @Override
    public Page<AuthorityWLModel> getAuthorityWebList(AuthorityWLCondition condition) {
        return authorityInfoMapper.getAuthorityWebList(condition);
    }

    @Override
    public List<GroupAuthorityLModel> getAllAuthorities() {
        return authorityInfoMapper.getAllAuthorities();
    }

    @Override
    public List<String> getAssociatedControlByUserId(String userId) {
        return authorityInfoMapper.getAssociatedControlByUserId(userId);
    }
}
