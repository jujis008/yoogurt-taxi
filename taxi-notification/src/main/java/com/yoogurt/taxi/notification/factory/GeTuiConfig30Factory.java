package com.yoogurt.taxi.notification.factory;

import com.yoogurt.taxi.notification.config.getui.GeTuiConfig30;
import com.yoogurt.taxi.notification.config.getui.IGeTuiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("config30Factory")
public class GeTuiConfig30Factory implements GeTuiConfigFactory {

    @Autowired
    private GeTuiConfig30 config30;

    @Override
    public IGeTuiConfig generateConfig() {
        return config30;
    }
}
