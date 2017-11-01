package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.beans.FinanceAppSettings;

public interface AppSettingService {

    FinanceAppSettings getAppSettings(String appId);

    FinanceAppSettings addAppSettings(FinanceAppSettings settings);

    FinanceAppSettings updateAppSettings(FinanceAppSettings settings);
}
