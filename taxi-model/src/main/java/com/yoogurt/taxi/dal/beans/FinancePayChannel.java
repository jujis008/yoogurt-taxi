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
@Table(name = "finance_pay_channel")
public class FinancePayChannel extends SuperModel {
    /**
     * 渠道ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * enable-生效，disable-禁用
     */
    @Column(name = "channel_status")
    private String channelStatus;

    /**
     * 渠道名称
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 渠道标签，比如：App支付，网站支付，分期贷
     */
    @Column(name = "channel_tag")
    private String channelTag;

}