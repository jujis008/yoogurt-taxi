package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.AbstractBatchDao;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.mapper.CommonResourceMapper;
import com.yoogurt.taxi.order.dao.ResourceDao;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceDaoImplAbstract extends AbstractBatchDao<CommonResourceMapper, CommonResource> implements ResourceDao {
}
