package com.yoogurt.taxi.system.service.impl;

import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;
import com.yoogurt.taxi.system.dao.AppVersionDao;
import com.yoogurt.taxi.system.service.AppVersionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class AppVersionServiceImpl implements AppVersionService {
    @Autowired
    private AppVersionDao appVersionDao;
    @Override
    public AppVersion getLatestOne(AppType appType) {
        Example example = new Example(AppVersion.class);
        example.setOrderByClause(" gmt_create desc");
        example.createCriteria()
                .andEqualTo("appType",appType.getCode())
                .andEqualTo("isDeleted", Boolean.FALSE);
        List<AppVersion> appVersionList = appVersionDao.selectByExample(example);
        if (CollectionUtils.isEmpty(appVersionList)) {
            return null;
        }
        return appVersionList.get(0);
    }
}
