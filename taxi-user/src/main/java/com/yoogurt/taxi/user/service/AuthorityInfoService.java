package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWebListCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWebListModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityListModel;

import java.util.List;

/**
 * 权限管理
 */

public interface AuthorityInfoService {

    ResponseObj saveAuthorityInfo(AuthorityInfo authorityInfo);

    AuthorityInfo getAuthorityById(Long authorityId);

    ResponseObj removeAuthorityById(Long authorityId);

    BasePager<AuthorityWebListModel> getAuthorityWebList(AuthorityWebListCondition condition);

    List<GroupAuthorityListModel> getAllAuthorities();

    List<String> getAssociatedControlByUserId(String userId);
}
