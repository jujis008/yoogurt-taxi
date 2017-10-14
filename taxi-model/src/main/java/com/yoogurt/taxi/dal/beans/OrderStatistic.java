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
@Table(name = "order_statistic")
public class OrderStatistic extends SuperModel{
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 交易量
     */
    @Column(name = "order_count")
    private Integer orderCount;

    /**
     * 总分5分，保留一位小数
     */
    private BigDecimal score;

    /**
     * 违章数量
     */
    @Column(name = "traffic_violation_count")
    private Integer trafficViolationCount;

    /**
     * 违约数量
     */
    @Column(name = "disobey_count")
    private Integer disobeyCount;
}