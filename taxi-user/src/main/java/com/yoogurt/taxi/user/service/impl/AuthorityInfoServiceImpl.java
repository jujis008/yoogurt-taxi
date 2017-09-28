package com.yoogurt.taxi.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.AuthorityInfo;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.model.user.AuthorityWLModel;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
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
        if (authorityInfo == null) {
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
    public Pager<AuthorityWLModel> getAuthorityWebList(AuthorityWLCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize(),"gmt_modify desc");
        Page<AuthorityWLModel> authorityWebList = authorityDao.getAuthorityWebList(condition);
        return webPagerFactory.generatePager(authorityWebList);
    }

    @Override
    public List<GroupAuthorityLModel> getAllAuthorities() {
        return authorityDao.getAllAuthorities();
    }
}
