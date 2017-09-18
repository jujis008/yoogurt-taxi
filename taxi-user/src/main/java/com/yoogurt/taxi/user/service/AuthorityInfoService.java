package com.yoogurt.taxi.user.service;

import com.yoogurt.taxi.dal.beans.AuthorityInfo;

/**
 * 权限管理
 */
public interface AuthorityInfoService {

    boolean saveAuthorityInfo(AuthorityInfo authorityInfo);

    AuthorityInfo getAuthorityById(Long authorityId);

    boolean removeAuthorityById(Long authorityId);

}
