package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.*;
import java.util.Date;

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
     * @return user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return lng
     */
    public String getLng() {
        return lng;
    }

    /**
     * @param lng
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     * @return lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * @param lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * 获取国标地区编码
     *
     * @return adcode - 国标地区编码
     */
    public String getAdcode() {
        return adcode;
    }

    /**
     * 设置国标地区编码
     *
     * @param adcode 国标地区编码
     */
    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取是否首选
     *
     * @return is_primary - 是否首选
     */
    public Byte getIsPrimary() {
        return isPrimary;
    }

    /**
     * 设置是否首选
     *
     * @param isPrimary 是否首选
     */
    public void setIsPrimary(Byte isPrimary) {
        this.isPrimary = isPrimary;
    }

}