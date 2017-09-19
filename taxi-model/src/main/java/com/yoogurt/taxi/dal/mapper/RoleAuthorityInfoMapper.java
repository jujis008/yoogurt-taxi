package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
import com.yoogurt.taxi.dal.model.AuthorityModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleAuthorityInfoMapper extends Mapper<RoleAuthorityInfo> {

    List<AuthorityModel> getAuthoritiesByRoleId(Long roleId);
}