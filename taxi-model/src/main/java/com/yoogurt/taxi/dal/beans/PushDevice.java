package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Domain
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "push_device")
public class PushDevice extends SuperModel {
    /**
     * 设备唯一标识，个推的client_id
     */
    @Id
    @Column(name = "client_id")
    private String clientId;

    /**
     * 绑定的用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 设备类型：android-安卓，iOS-苹果
     */
    @Column(name = "device_type")
    private String deviceType;

    /**
     * 设备名称，如iPhone 6s xiaomi4
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 操作系统版本号
     */
    @Column(name = "os_version")
    private String osVersion;

    /**
     * 设备状态 10-未绑定, 20-已绑定, 30-黑名单
     */
    private Integer status;

    public PushDevice(String clientId) {
        this.clientId = clientId;
    }
}