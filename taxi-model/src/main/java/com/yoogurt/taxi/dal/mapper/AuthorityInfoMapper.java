package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuthorityInfoMapper extends Mapper<AuthorityInfo> {
    Page<AuthorityWLModel> getAuthorityWebList(AuthorityWLCondition condition);
    List<GroupAuthorityLModel> getAllAuthorities();
    List<String> getAssociatedControlByUserId(String userId);
}