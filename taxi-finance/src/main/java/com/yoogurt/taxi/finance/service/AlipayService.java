package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.beans.FinanceAlipaySettings;
import com.yoogurt.taxi.pay.service.PayChannelService;

public interface AlipayService extends PayChannelService {

    FinanceAlipaySettings addAlipaySettings(FinanceAlipaySettings settings);

    FinanceAlipaySettings updateAlipaySettings(FinanceAlipaySettings settings);

    FinanceAlipaySettings getAlipaySettings(String appId);

    FinanceAlipaySettings getAlipaySettingsByAppId(String alipayAppId);
}
