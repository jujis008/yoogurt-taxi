package com.yoogurt.taxi.finance.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.dal.mapper.FinanceWxSettingsMapper;
import com.yoogurt.taxi.finance.dao.WxSettingsDao;
import org.springframework.stereotype.Repository;

@Repository
public class WxSettingsDaoImpl extends BaseDao<FinanceWxSettingsMapper, FinanceWxSettings> implements WxSettingsDao {
}
