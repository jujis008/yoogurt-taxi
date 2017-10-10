package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IBatchDao;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.dal.mapper.CommonResourceMapper;

public interface ResourceDao extends IBatchDao<CommonResourceMapper, CommonResource> {
}
