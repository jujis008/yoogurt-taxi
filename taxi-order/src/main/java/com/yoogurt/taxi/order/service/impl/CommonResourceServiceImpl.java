package com.yoogurt.taxi.order.service.impl;

import com.google.common.collect.Lists;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.order.dao.ResourceDao;
import com.yoogurt.taxi.order.service.CommonResourceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CommonResourceServiceImpl implements CommonResourceService {

    @Autowired
    private ResourceDao resourceDao;

    @Override
    public List getResources(String linkId, String tableName) {
        CommonResource probe = new CommonResource();
        probe.setIsDeleted(Boolean.FALSE);
        probe.setLinkId(linkId);
        probe.setTableName(tableName);
        return resourceDao.selectList(probe);
    }

    @Override
    public int addResources(List<CommonResource> resources) {
        if(CollectionUtils.isEmpty(resources)) return 0;
        return resourceDao.insertList(resources);
    }

    @Override
    public int removeResources(String linkId, String tableName) {
        CommonResource resource = new CommonResource();
        resource.setIsDeleted(Boolean.TRUE);
        Example example = new Example(CommonResource.class);
        example.createCriteria().andEqualTo("linkId", linkId).andEqualTo("tableName", tableName);
        return resourceDao.updateByExampleSelective(resource, example);
    }

    @Override
    public List<CommonResource> assembleResources(String linkId, String tableName, String... pictures) {
        List<CommonResource> resources = Lists.newArrayList();
        if(StringUtils.isBlank(linkId) || pictures == null || pictures.length == 0) return resources;
        for (String picture : pictures) {
            CommonResource resource = new CommonResource();
            resource.setLinkId(linkId);
            resource.setTableName(tableName);
            resource.setUrl(picture);
            resource.setResourceName(picture.substring(picture.lastIndexOf("/") + 1));
            resources.add(resource);
        }
        return resources;
    }
}
