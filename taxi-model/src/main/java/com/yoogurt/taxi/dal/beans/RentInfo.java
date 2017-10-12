package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import com.yoogurt.taxi.dal.enums.RentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "rent_info")
public class RentInfo extends SuperModel{
    @Id
    @Column(name = "rent_id")
    private Long rentId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_name")
    private String driverName;

    /**
     * 20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     */
    @Column(name = "user_type")
    private Integer userType;

    private String mobile;

    private String avatar;

    @Column(name = "handover_time")
    private Date handoverTime;

    @Column(name = "give_back_time")
    private Date giveBackTime;

    private String address;

    private Double lng;

    private Double lat;

    private BigDecimal price;

    /**
     * 10-待接单，20-已接单，30-手动取消，40-超时自动取消
     */
    private Integer status;

    @Column(name = "car_id")
    private Long carId;

    @Column(name = "plate_number")
    private String plateNumber;

    private String company;

    /**
     * 10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    @Column(name = "energy_type")
    private Integer energyType;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "car_thumb")
    private String carThumb;

    private String vin;

    private String remark;

    public RentInfo() {
    }

    public RentInfo(Long rentId) {
        this.rentId = rentId;
        this.status = RentStatus.WAITING.getCode();
    }
}