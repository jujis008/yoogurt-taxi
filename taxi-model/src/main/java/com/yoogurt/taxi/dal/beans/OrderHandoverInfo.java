package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_handover_info")
public class OrderHandoverInfo extends SuperModel{
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 如果没有违约，规则id为0
     */
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "real_handover_address")
    private String realHandoverAddress;

    private Double lng;

    private Double lat;

    /**
     * 违约罚款
     */
    @Column(name = "fine_money")
    private BigDecimal fineMoney;

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