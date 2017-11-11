package com.yoogurt.taxi.dal.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
@Table(name = "user_address")
public class UserAddress extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

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

}