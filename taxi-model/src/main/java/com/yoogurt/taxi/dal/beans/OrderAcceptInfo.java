package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_accept_info")
public class OrderAcceptInfo extends SuperModel{
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 0-异常，1-正常
     */
    @Column(name = "car_status")
    private Boolean carStatus;

    /**
     * 异常描述
     */
    private String description;

}