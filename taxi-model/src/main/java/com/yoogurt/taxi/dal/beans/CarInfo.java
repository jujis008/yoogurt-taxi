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
@Table(name = "car_info")
public class CarInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "driver_id")
    private Long driverId;

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
     * 能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    @Column(name = "energy_type")
    private Integer energyType;

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
    private Date vehicleRegisterTime;

}