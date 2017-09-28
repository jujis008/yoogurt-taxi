package com.yoogurt.taxi.dal.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.dal.annotation.Domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "driver_info")
public class DriverInfo{
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
    @JsonIgnore
    private Boolean isAuthentication;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 创建人ID
     */
    @Column(name = "creator")
    @JsonIgnore
    private Long creator;

    /**
     * 最后修改时间
     */
    @Column(name = "gmt_modify")
    private Date gmtModify;

    /**
     * 最后修改人ID
     */
    @Column(name = "modifier")
    @JsonIgnore
    private Long modifier;
}