package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限管理
 */

public interface AuthorityInfoService {

    ResponseObj saveAuthorityInfo(AuthorityInfo authorityInfo);

    AuthorityInfo getAuthorityById(Long authorityId);

    ResponseObj removeAuthorityById(Long authorityId);

    Pager<AuthorityWLModel> getAuthorityWebList(AuthorityWLCondition condition);

    List<GroupAuthorityLModel> getAllAuthorities();

    List<String> getAssociatedControlByUserId(String userId);
}
