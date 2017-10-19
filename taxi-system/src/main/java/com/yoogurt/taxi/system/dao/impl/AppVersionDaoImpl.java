package com.yoogurt.taxi.system.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.mapper.AppVersionMapper;
import com.yoogurt.taxi.system.dao.AppVersionDao;
import org.springframework.stereotype.Repository;

@Repository
public class AppVersionDaoImpl extends BaseDao<AppVersionMapper,AppVersion> implements AppVersionDao {
}
