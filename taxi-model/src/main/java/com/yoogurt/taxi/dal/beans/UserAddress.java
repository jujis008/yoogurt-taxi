package com.yoogurt.taxi.dal.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.dal.annotation.Domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private Double lng;

    private Double lat;

    /**
     * 国标地区编码
     */
    private String adcode;

    private String area;

    private String address;

    /**
     * 是否首选
     */
    @Column(name = "is_primary")
    private Boolean isPrimary;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 创建人ID
     */
    @Column(name = "creator")
    @JsonIgnore
    private Long creator;

    /**
     * 最后修改时间
     */
    @Column(name = "gmt_modify")
    private Date gmtModify;

    /**
     * 最后修改人ID
     */
    @Column(name = "modifier")
    @JsonIgnore
    private Long modifier;
}