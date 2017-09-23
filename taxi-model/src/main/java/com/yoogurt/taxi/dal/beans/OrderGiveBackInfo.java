package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_give_back_info")
public class OrderGiveBackInfo extends SuperModel{
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "real_give_back_address")
    private String realGiveBackAddress;

    private Double lng;

    private Double lat;

    /**
     * 0-否，1-是
     */
    @Column(name = "is_disobey")
    private Boolean isDisobey;

    /**
     * 指的是取消时间到交车时间的间隔，按照向上取整的原则计算
     */
    private Integer time;

    /**
     * 时长单位：SECONDS-秒，MINUTES-分，HOURS-小时
     */
    private String unit;

}