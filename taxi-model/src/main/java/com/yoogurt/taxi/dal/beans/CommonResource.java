package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "common_resource")
public class CommonResource extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id")
    private String linkId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "resource_name")
    private String resourceName;

    private String url;

    /**
     * 指占用磁盘空间大小，单位：字节
     */
    private Integer size;
}