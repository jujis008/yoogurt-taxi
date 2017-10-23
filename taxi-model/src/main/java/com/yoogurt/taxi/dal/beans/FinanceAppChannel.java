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
@Table(name = "finance_app_channel")
public class FinanceAppChannel extends SuperModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 渠道ID
     */
    @Column(name = "channel_id")
    private Integer channelId;

    /**
     * 渠道名称
     */
    @Column(name = "channel_name")
    private String channelName;

}