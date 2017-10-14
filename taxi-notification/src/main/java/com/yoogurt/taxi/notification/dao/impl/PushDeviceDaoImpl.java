package com.yoogurt.taxi.notification.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.mapper.PushDeviceMapper;
import com.yoogurt.taxi.notification.dao.PushDeviceDao;
import org.springframework.stereotype.Repository;

@Repository
public class PushDeviceDaoImpl extends BaseDao<PushDeviceMapper, PushDevice> implements PushDeviceDao {
}
