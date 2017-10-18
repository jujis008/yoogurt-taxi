package com.yoogurt.taxi.notification.factory;

import com.yoogurt.taxi.notification.config.getui.GeTuiConfig20;
import com.yoogurt.taxi.notification.config.getui.IGeTuiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("config20Factory")
public class GeTuiConfig20Factory implements GeTuiConfigFactory {

    @Autowired
    private GeTuiConfig20 config20;

    @Override
    public IGeTuiConfig generateConfig() {
        return config20;
    }
}
