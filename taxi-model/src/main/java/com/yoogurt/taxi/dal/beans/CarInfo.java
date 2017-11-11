package com.yoogurt.taxi.dal.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.enums.CarEnergyType;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "car_info")
public class CarInfo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    @Column(name = "driver_id")
    private String driverId;

    /**
     * 车牌号
     */
    @Column(name = "plate_number")
    private String plateNumber;

    /**
     * 车前照
     */
    @Column(name = "car_picture")
    private String carPicture;

    /**
     * 车型
     */
    @Column(name = "vehicle_type")
    private String vehicleType;

    /**
     * 能源：10（GASOLINE）-汽油，20-GAS-天然气，30（POWER）-电能，40（HYBRID）-混合，50（OTHER）-其它
     */
    @Column(name = "energy_type")
    private Integer energyType;

    /**
     * 发动机号
     */
    @Column(name = "engine_number")
    private String engineNumber;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 车辆所属单位
     */
    private String company;

    /**
     * 车辆登记时间
     */
    @Column(name = "vehicle_register_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date vehicleRegisterTime;

    /**
     * 行车证正面照
     */
    @Column(name = "driving_permit_front")
    private String drivingPermitFront;

    /**
     * 行车证反面照
     */
    @Column(name = "driving_permit_back")
    private String drivingPermitBack;

    /**
     * 交强险
     */
    @Column(name = "compulsory_insurance")
    private String compulsoryInsurance;

    /**
     * 商业险
     */
    @Column(name = "commercial_insurance")
    private String commercialInsurance;

    /**
     * 是否认证
     */
    @Column(name = "is_authentication")
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
    private String creator;

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
    private String modifier;

}