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
@Table(name = "order_handover_rule")
public class OrderHandoverRule extends SuperModel{
    @Id
    @Column(name = "rule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    /**
     * 计算时，按照向上取整的原则
     */
    private Integer time;

    /**
     * 时长单位：SECONDS-秒，MINUTES-分，HOURS-小时
     */
    private String unit;

    private BigDecimal price;

}