package com.yoogurt.taxi.finance.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FinanceAlipaySettings;
import com.yoogurt.taxi.dal.mapper.FinanceAlipaySettingsMapper;
import com.yoogurt.taxi.finance.dao.AlipaySettingsDao;
import org.springframework.stereotype.Repository;

@Repository
public class AlipaySettingsDaoImpl extends BaseDao<FinanceAlipaySettingsMapper, FinanceAlipaySettings> implements AlipaySettingsDao {
}
