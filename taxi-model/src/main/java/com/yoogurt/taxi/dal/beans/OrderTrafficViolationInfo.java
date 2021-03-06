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
    private String orderId;

    /**
     * 录入违章的用户ID，通常为车主
     */
    @Column(name = "input_user_id")
    private String inputUserId;

    /**
     * 违章的用户ID，通常为司机
     */
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