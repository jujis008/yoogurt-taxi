package com.yoogurt.taxi.system.service;

import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;

public interface AppVersionService {
    AppVersion getLatestOne(AppType appType);
}
