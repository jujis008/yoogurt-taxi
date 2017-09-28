package com.yoogurt.taxi.user.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RoleAuthorityInfo;
import com.yoogurt.taxi.dal.beans.RoleInfo;
import com.yoogurt.taxi.dal.beans.UserRoleInfo;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;
import com.yoogurt.taxi.user.dao.RoleAuthorityDao;
import com.yoogurt.taxi.user.dao.RoleDao;
import com.yoogurt.taxi.user.dao.UserRoleDao;
import com.yoogurt.taxi.user.service.RoleInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class RoleInfoServiceImpl implements RoleInfoService{
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleAuthorityDao roleAuthorityDao;
    @Override
    public ResponseObj saveRoleInfo(RoleInfo roleInfo) {
        Example example = new Example(RoleInfo.class);
        example.createCriteria().andEqualTo("roleCode",roleInfo.getRoleCode())
        .andEqualTo("isDeleted",Boolean.FALSE);
        List<RoleInfo> roleInfoList = roleDao.selectByExample(example);
        if (roleInfo.getId() != null) {
            if (roleInfoList.size()>1||roleInfoList.get(0).getId().equals(roleInfo.getId())){
                return ResponseObj.fail(StatusCode.BIZ_FAILED,"角色代码已重复");
            }
            roleDao.updateById(roleInfo);
        }else {
            if (CollectionUtils.isNotEmpty(roleInfoList)) {
                return ResponseObj.fail(StatusCode.BIZ_FAILED,"角色代码已重复");
            }
            roleDao.insert(roleInfo);
        }
        return ResponseObj.success();
    }

    @Override
    public ResponseObj removeRole(Long roleId) {
        RoleInfo roleInfo = roleDao.selectById(roleId);
        if(roleInfo == null) {
            return ResponseObj.success();
        }
        roleInfo.setIsDeleted(Boolean.TRUE);
        roleDao.updateById(roleInfo);

        Example example0 = new Example(UserRoleInfo.class);
        example0.createCriteria().andEqualTo("isDeleted",Boolean.FALSE)
                .andEqualTo("roleId",roleId);
        List<UserRoleInfo> userRoleInfoList = userRoleDao.selectByExample(example0);
        for (UserRoleInfo userRoleInfo:userRoleInfoList) {
            userRoleInfo.setIsDeleted(Boolean.TRUE);
            userRoleDao.updateById(userRoleInfo);
        }

        Example example1 = new Example(RoleAuthorityInfo.class);
        example1.createCriteria().andEqualTo("isDeleted", Boolean.FALSE)
                .andEqualTo("roleId",roleId);
        List<RoleAuthorityInfo> roleAuthorityInfoList = roleAuthorityDao.selectByExample(example1);
        for (RoleAuthorityInfo roleAuthorityInfo:roleAuthorityInfoList) {
            roleAuthorityInfo.setIsDeleted(Boolean.TRUE);
            roleAuthorityDao.updateById(roleAuthorityInfo);
        }

        return ResponseObj.success();
    }

    @Override
    public ResponseObj getRoleById(Long roleId) {
        return ResponseObj.success(roleDao.selectById(roleId));
    }

    @Override
    public List<RoleWLModel> getRoleWebList() {
        return roleDao.getRoleWebList();
    }
}
