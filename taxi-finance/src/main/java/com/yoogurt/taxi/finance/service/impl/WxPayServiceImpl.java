package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.dal.beans.FinanceWxSettings;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.dao.WxSettingsDao;
import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.finance.task.PayTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private WxSettingsDao wxSettingsDao;

    @Override
    public FinanceWxSettings addWxSettings(FinanceWxSettings settings) {
        if (settings == null) return null;
        if (wxSettingsDao.insertSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceWxSettings updateWxSettings(FinanceWxSettings settings) {
        if (settings == null) return null;
        if (wxSettingsDao.updateByIdSelective(settings) == 1) return settings;
        return null;
    }

    @Override
    public FinanceWxSettings getWxSettings(String appId) {
        if (StringUtils.isBlank(appId)) return null;
        return wxSettingsDao.selectById(appId);
    }

    @Override
    public CompletableFuture<Payment> doTask(PayTask payTask) {
        return null;
    }
}
