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

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return link_id
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * @param linkId
     */
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    /**
     * @return table_name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return resource_name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取指占用磁盘空间大小，单位：字节
     *
     * @return size - 指占用磁盘空间大小，单位：字节
     */
    public Integer getSize() {
        return size;
    }

    /**
     * 设置指占用磁盘空间大小，单位：字节
     *
     * @param size 指占用磁盘空间大小，单位：字节
     */
    public void setSize(Integer size) {
        this.size = size;
    }
}