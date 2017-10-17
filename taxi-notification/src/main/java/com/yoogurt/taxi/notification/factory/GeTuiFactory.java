package com.yoogurt.taxi.notification.factory;

import com.yoogurt.taxi.common.utils.SpringContextHolder;
import com.yoogurt.taxi.dal.enums.UserType;

public class GeTuiFactory {

    public static GeTuiConfigFactory getConfigFactory(UserType type) {
        switch (type) {
            case USER_APP_AGENT:
                return SpringContextHolder.getBean("config20Factory");
            case USER_APP_OFFICE:
                return SpringContextHolder.getBean("config30Factory");
            default:
                return new GeTuiDefaultFactory();
        }
    }
}
