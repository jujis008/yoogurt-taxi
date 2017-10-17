package com.yoogurt.taxi.notification.factory;

import com.yoogurt.taxi.notification.config.GeTuiDefaultConfig;
import com.yoogurt.taxi.notification.config.IGeTuiConfig;

public class GeTuiDefaultFactory implements GeTuiConfigFactory {
    
    @Override
    public IGeTuiConfig generateConfig() {

        return new GeTuiDefaultConfig();
    }
}
