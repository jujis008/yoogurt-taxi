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
@Table(name = "finance_app_settings")
public class FinanceAppSettings extends SuperModel {
    /**
     * 应用ID
     */
    @Id
    @Column(name = "app_id")
    private String appId;

    /**
     * 应用名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * open-正常开启状态，close-关闭状态
     */
    private String status;

}