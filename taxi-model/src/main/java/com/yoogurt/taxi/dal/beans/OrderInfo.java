package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_info")
public class OrderInfo extends SuperModel{
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "rent_id")
    private Long rentId;

    @Column(name = "agent_user_id")
    private Long agentUserId;

    @Column(name = "agent_driver_id")
    private Long agentDriverId;

    @Column(name = "agent_driver_name")
    private String agentDriverName;

    @Column(name = "agent_driver_phone")
    private String agentDriverPhone;

    @Column(name = "handover_time")
    private Date handoverTime;

    @Column(name = "give_back_time")
    private Date giveBackTime;

    private String company;

    private String address;

    private Double lng;

    private Double lat;

    /**
     * 单价
     */
    private BigDecimal price;

    @Column(name = "car_id")
    private Long carId;

    @Column(name = "plate_number")
    private String plateNumber;

    private String vin;

    /**
     * 10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    @Column(name = "energy_type")
    private Integer energyType;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "car_thumb")
    private String carThumb;

    /**
     * 10-待交车，20-待取车，30-待还车，40-待收车，50-已完成，60-已取消
     */
    private Integer status;

    @Column(name = "official_user_id")
    private Long officialUserId;

    @Column(name = "official_driver_id")
    private Long officialDriverId;

    @Column(name = "official_driver_name")
    private String officialDriverName;

    @Column(name = "official_driver_phone")
    private String officialDriverPhone;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 0-否，1-是
     */
    @Column(name = "is_commented")
    private Boolean isCommented;

    /**
     * 0-否，1-是
     */
    @Column(name = "is_paid")
    private Boolean isPaid;

    private String remark;

    public OrderInfo() {
    }

    public OrderInfo(Long orderId) {
        this.orderId = orderId;
        this.status = OrderStatus.HAND_OVER.getCode();
        this.isPaid = false;
        this.isCommented = false;

    }
}