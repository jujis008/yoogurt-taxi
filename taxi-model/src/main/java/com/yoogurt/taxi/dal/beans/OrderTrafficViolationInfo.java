package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_traffic_violation_info")
public class OrderTrafficViolationInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "order_id")
    private Long orderId;

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

    @Column(name = "car_id")
    private Long carId;

    @Column(name = "plate_number")
    private String plateNumber;

    private String vin;

    /**
     * 10-服务违章，20-交通违章
     */
    private Integer type;

    @Column(name = "happen_time")
    private Date happenTime;

    private String address;

    private String description;

    /**
     * 10-未处理，20-已处理，30-信息误报
     */
    private Integer status;

}