package com.yoogurt.taxi.dal.beans;

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
    private Long userId;

    private String lng;

    private String lat;

    /**
     * 国标地区编码
     */
    private String adcode;

    private String address;

    /**
     * 是否首选
     */
    @Column(name = "is_primary")
    private Byte isPrimary;

}