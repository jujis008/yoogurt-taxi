package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "driver_info")
public class DriverInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String mobile;

    /**
     * 类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     */
    private Integer type;

    /**
     * 性别：10-MALE，20-FEMALE,30-SECRET
     */
    private Integer gender;

    /**
     * 出生年月：xxxx年xx月
     */
    private Date birthday;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 驾驶证号
     */
    @Column(name = "driving_license")
    private String drivingLicense;

    /**
     * 户籍
     */
    private String household;

    /**
     * 现居住地址
     */
    private String address;

    /**
     * 服务资格证号
     */
    @Column(name = "service_number")
    private String serviceNumber;

    @Column(name = "service_picture")
    private String servicePicture;

    @Column(name = "id_front")
    private String idFront;

    @Column(name = "id_back")
    private String idBack;

    @Column(name = "driving_license_front")
    private String drivingLicenseFront;

    @Column(name = "driving_license_back")
    private String drivingLicenseBack;

    /**
     * 是否认证
     */
    @Column(name = "is_authentication")
    private Boolean isAuthentication;
}