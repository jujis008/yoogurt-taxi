package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_pick_up_info")
public class OrderPickUpInfo extends SuperModel{
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private String orderId;

    /**
     * 车辆状态：false-异常，true-正常
     */
    @Column(name = "car_status")
    private Boolean carStatus;

    /**
     * 异常描述
     */
    private String description;

}