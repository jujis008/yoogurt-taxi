package com.yoogurt.taxi.finance.service;

public abstract class AbstractFinanceBizService implements PayChannelService {



    public String getNotifyUrl() {
        return "http://m.yoogate.cn/common/finance/i/notify";
    }

}
