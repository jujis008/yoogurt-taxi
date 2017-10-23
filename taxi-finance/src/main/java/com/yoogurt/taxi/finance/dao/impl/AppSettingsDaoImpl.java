package com.yoogurt.taxi.finance.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceAppSettings;
import com.yoogurt.taxi.dal.mapper.FinanceAppSettingsMapper;
import com.yoogurt.taxi.finance.dao.AppSettingsDao;
import org.springframework.stereotype.Repository;

@Repository
public class AppSettingsDaoImpl extends BaseDao<FinanceAppSettingsMapper, FinanceAppSettings> implements AppSettingsDao {
}
