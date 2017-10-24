package com.yoogurt.taxi.system.service;

import com.yoogurt.taxi.dal.beans.AppVersion;
import com.yoogurt.taxi.dal.enums.AppType;
import com.yoogurt.taxi.dal.enums.SysType;

import java.util.List;

public interface AppVersionService {
    AppVersion getLatestOne(AppType appType, SysType sysType);
    List<AppVersion> getList();

    int save(AppVersion appVersion);

    int remove(Long id);
}
