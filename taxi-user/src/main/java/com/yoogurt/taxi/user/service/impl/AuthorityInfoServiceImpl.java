package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWebListCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWebListModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityListModel;
import com.yoogurt.taxi.user.dao.AuthorityDao;
import com.yoogurt.taxi.user.service.AuthorityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AuthorityInfoServiceImpl implements AuthorityInfoService{
    @Autowired
    private AuthorityDao    authorityDao;
    @Autowired
    private WebPagerFactory webPagerFactory;

    @Override
    public ResponseObj saveAuthorityInfo(AuthorityInfo authorityInfo) {
        if (authorityInfo.getId() == null) {
            authorityDao.insert(authorityInfo);
        } else {
            authorityDao.updateByIdSelective(authorityInfo);
        }
        return ResponseObj.success(authorityInfo.getId());
    }

    @Override
    public AuthorityInfo getAuthorityById(Long authorityId) {
        return authorityDao.selectById(authorityId);
    }

    @Override
    public ResponseObj removeAuthorityById(Long authorityId) {
        AuthorityInfo authorityInfo = authorityDao.selectById(authorityId);
        authorityInfo.setIsDeleted(Boolean.TRUE);
        authorityDao.updateById(authorityInfo);
        return ResponseObj.success();
    }

    @Override
    public BasePager<AuthorityWebListModel> getAuthorityWebList(AuthorityWebListCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize(),"gmt_modify desc");
        Page<AuthorityWebListModel> authorityWebList = authorityDao.getAuthorityWebList(condition);
        return webPagerFactory.generatePager(authorityWebList);
    }

    @Override
    public List<GroupAuthorityListModel> getAllAuthorities() {
        return authorityDao.getAllAuthorities();
    }

    @Override
    public List<String> getAssociatedControlByUserId(String userId) {
        return authorityDao.getAssociatedControlByUserId(userId);
    }
}
