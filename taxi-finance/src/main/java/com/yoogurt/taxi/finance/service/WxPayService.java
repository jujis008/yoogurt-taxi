package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.pay.service.PayChannelService;

public interface WxPayService extends PayChannelService {

    FinanceWxSettings addWxSettings(FinanceWxSettings settings);

    FinanceWxSettings updateWxSettings(FinanceWxSettings settings);

    FinanceWxSettings getWxSettings(String appId);

    FinanceWxSettings getWxSettingsByAppId(String wxAppId);

}
