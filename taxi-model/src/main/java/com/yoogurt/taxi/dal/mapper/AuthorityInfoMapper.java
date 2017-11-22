package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWebListCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWebListModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityListModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuthorityInfoMapper extends Mapper<AuthorityInfo> {
    Page<AuthorityWebListModel> getAuthorityWebList(AuthorityWebListCondition condition);
    List<GroupAuthorityListModel> getAllAuthorities();
    List<String> getAssociatedControlByUserId(String userId);
}