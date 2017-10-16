package com.yoogurt.taxi.dal.enums;

import org.apache.commons.lang.StringUtils;

/**
 * <p class="detail">
 * 功能：设备类型
    all-全部, 
    android-安卓设备, 
    IOS-苹果设备
 * </p>
 * @ClassName: SendType 
 * @version V1.0  
 * @date 2016年12月30日 
 * @author weihao.liu
 * Copyright 2016 tsou.com, Inc. All rights reserved
 */
public enum DeviceType {

    ALL("all", "全部设备"), 
    ANDROID("android", "安卓设备"), 
    IOS("IOS", "苹果设备");

    private String  type;

    private String detail;

    DeviceType(String type, String detail) {
        this.type = type;
        this.detail = detail;
    }

    public static DeviceType getEnumByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (DeviceType userType : DeviceType.values()) {
                if (type.equals(userType.getType())) {
                    return userType;
                }
            }
        }
        return null;
    }

    public static String getDetailByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (DeviceType userType : DeviceType.values()) {
                if (type.equals(userType.getType())) {
                    return userType.getDetail();
                }
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
