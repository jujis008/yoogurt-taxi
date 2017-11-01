package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.dal.beans.FinanceAppSettings;
import com.yoogurt.taxi.finance.dao.AppSettingsDao;
import com.yoogurt.taxi.finance.service.AppSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppSettingServiceImpl implements AppSettingService {

    @Autowired
    private AppSettingsDao appSettingsDao;

    @Override
    public FinanceAppSettings getAppSettings(String appId) {
        if(StringUtils.isBlank(appId)) return null;
        return appSettingsDao.selectById(appId);
    }

    @Override
    public FinanceAppSettings addAppSettings(FinanceAppSettings settings) {
        if(settings == null) return null;
        if(appSettingsDao.insertSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceAppSettings updateAppSettings(FinanceAppSettings settings) {
        if(settings == null) return null;
        if(appSettingsDao.updateByIdSelective(settings) == 1) return settings;
        return null;
    }
}
