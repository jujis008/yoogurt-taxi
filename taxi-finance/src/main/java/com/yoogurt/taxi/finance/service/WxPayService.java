package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.beans.FinanceWxSettings;

public interface WxPayService extends PayChannelService {

    FinanceWxSettings addWxSettings(FinanceWxSettings settings);

    FinanceWxSettings updateWxSettings(FinanceWxSettings settings);

    FinanceWxSettings getWxSettings(String appId);

    FinanceWxSettings getWxSettingsByAppId(String wxAppId);

}
