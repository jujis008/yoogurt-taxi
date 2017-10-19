package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
@Table(name = "app_version")
@Domain
public class AppVersion extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用类型：1-offfice,2-agent
     */
    @Column(name = "app_type")
    private Integer appType;

    /**
     * 版本名称
     */
    @Column(name = "version_name")
    private String versionName;

    /**
     * 版本号
     */
    @Column(name = "version_number")
    private String versionNumber;

    /**
     * 下载地址
     */
    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * 是否强制更新
     */
    @Column(name = "is_force")
    private Boolean isForce;

    /**
     * 版本说明
     */
    private String description;

}