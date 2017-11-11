package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "order_disobey_info")
public class OrderDisobeyInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "driver_name")
    private String driverName;

    /**
     * 20（USER_APP_AGENT）-代理端用户,30（USER_APP_OFFICE）-正式端用户
     */
    @Column(name = "user_type")
    private Integer userType;

    private String mobile;

    @Column(name = "happen_time")
    private Date happenTime;

    private String description;

    @Column(name = "car_id")
    private Long carId;

    @Column(name = "plate_number")
    private String plateNumber;

    private String vin;

    /**
     * 取消违约罚款
     */
    @Column(name = "fine_money")
    private BigDecimal fineMoney;

    /**
     * 10-车主交车超时，20-司机还车超时，30-司机取消(无责取消不记录违约)
     */
    private Integer type;

    @Column(name = "rule_id")
    private Long ruleId;

    /**
     * 0-未处理，1-已处理
     */
    private Boolean status;

    public OrderDisobeyInfo() {
    }

    public OrderDisobeyInfo(String orderId) {
        this.orderId = orderId;
    }

}