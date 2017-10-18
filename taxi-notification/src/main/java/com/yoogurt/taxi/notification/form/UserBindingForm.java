package com.yoogurt.taxi.notification.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class UserBindingForm {

    /**
     * 推送SDK生成的设备唯一标识
     */
    @NotBlank(message = "请指定设备")
    private String clientId;

    /**
     * 设备类型：android-安卓，iOS-苹果
     */
    private String deviceType;

    /**
     * 设备名称，如iPhone 6s xiaomi4
     */
    private String deviceName;

    /**
     * 操作系统版本号
     */
    private String osVersion;


    private Long userId;
}
