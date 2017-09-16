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
@Table(name = "driver_info")
public class DriverInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String mobile;

    /**
     * 类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     */
    private Integer type;

    /**
     * 性别：10-MALE，20-FEMALE,30-SECRET
     */
    private Integer gender;

    /**
     * 出生年月：xxxx年xx月
     */
    private String birthday;

    /**
     * 身份证号
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 驾驶证号
     */
    @Column(name = "driving_license")
    private String drivingLicense;

    /**
     * 户籍
     */
    private String household;

    /**
     * 现居住地址
     */
    private String address;

    /**
     * 服务资格证号
     */
    @Column(name = "service_number")
    private String serviceNumber;

    @Column(name = "service_picture")
    private String servicePicture;

    @Column(name = "id_front")
    private String idFront;

    @Column(name = "id_back")
    private String idBack;

    @Column(name = "driving_license_front")
    private String drivingLicenseFront;

    @Column(name = "driving_license_back")
    private String drivingLicenseBack;

    @Column(name = "driving_permit_front")
    private String drivingPermitFront;

    @Column(name = "driving_permit_back")
    private String drivingPermitBack;

    @Column(name = "compulsory_insurance")
    private String compulsoryInsurance;

    @Column(name = "commercial_insurance")
    private String commercialInsurance;


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
     * @return mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     *
     * @return type - 类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     *
     * @param type 类型：20-USER_APP_AGENT,30-USER_APP_OFFICE
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取性别：10-MALE，20-FEMALE,30-SECRET
     *
     * @return gender - 性别：10-MALE，20-FEMALE,30-SECRET
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * 设置性别：10-MALE，20-FEMALE,30-SECRET
     *
     * @param gender 性别：10-MALE，20-FEMALE,30-SECRET
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * 获取出生年月：xxxx年xx月
     *
     * @return birthday - 出生年月：xxxx年xx月
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * 设置出生年月：xxxx年xx月
     *
     * @param birthday 出生年月：xxxx年xx月
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取身份证号
     *
     * @return id_card - 身份证号
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置身份证号
     *
     * @param idCard 身份证号
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取驾驶证号
     *
     * @return driving_license - 驾驶证号
     */
    public String getDrivingLicense() {
        return drivingLicense;
    }

    /**
     * 设置驾驶证号
     *
     * @param drivingLicense 驾驶证号
     */
    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    /**
     * 获取户籍
     *
     * @return household - 户籍
     */
    public String getHousehold() {
        return household;
    }

    /**
     * 设置户籍
     *
     * @param household 户籍
     */
    public void setHousehold(String household) {
        this.household = household;
    }

    /**
     * 获取现居住地址
     *
     * @return address - 现居住地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置现居住地址
     *
     * @param address 现居住地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取服务资格证号
     *
     * @return service_number - 服务资格证号
     */
    public String getServiceNumber() {
        return serviceNumber;
    }

    /**
     * 设置服务资格证号
     *
     * @param serviceNumber 服务资格证号
     */
    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    /**
     * @return service_picture
     */
    public String getServicePicture() {
        return servicePicture;
    }

    /**
     * @param servicePicture
     */
    public void setServicePicture(String servicePicture) {
        this.servicePicture = servicePicture;
    }

    /**
     * @return id_front
     */
    public String getIdFront() {
        return idFront;
    }

    /**
     * @param idFront
     */
    public void setIdFront(String idFront) {
        this.idFront = idFront;
    }

    /**
     * @return id_back
     */
    public String getIdBack() {
        return idBack;
    }

    /**
     * @param idBack
     */
    public void setIdBack(String idBack) {
        this.idBack = idBack;
    }

    /**
     * @return driving_license_front
     */
    public String getDrivingLicenseFront() {
        return drivingLicenseFront;
    }

    /**
     * @param drivingLicenseFront
     */
    public void setDrivingLicenseFront(String drivingLicenseFront) {
        this.drivingLicenseFront = drivingLicenseFront;
    }

    /**
     * @return driving_license_back
     */
    public String getDrivingLicenseBack() {
        return drivingLicenseBack;
    }

    /**
     * @param drivingLicenseBack
     */
    public void setDrivingLicenseBack(String drivingLicenseBack) {
        this.drivingLicenseBack = drivingLicenseBack;
    }

    /**
     * @return driving_permit_front
     */
    public String getDrivingPermitFront() {
        return drivingPermitFront;
    }

    /**
     * @param drivingPermitFront
     */
    public void setDrivingPermitFront(String drivingPermitFront) {
        this.drivingPermitFront = drivingPermitFront;
    }

    /**
     * @return driving_permit_back
     */
    public String getDrivingPermitBack() {
        return drivingPermitBack;
    }

    /**
     * @param drivingPermitBack
     */
    public void setDrivingPermitBack(String drivingPermitBack) {
        this.drivingPermitBack = drivingPermitBack;
    }

    /**
     * @return compulsory_insurance
     */
    public String getCompulsoryInsurance() {
        return compulsoryInsurance;
    }

    /**
     * @param compulsoryInsurance
     */
    public void setCompulsoryInsurance(String compulsoryInsurance) {
        this.compulsoryInsurance = compulsoryInsurance;
    }

    /**
     * @return commercial_insurance
     */
    public String getCommercialInsurance() {
        return commercialInsurance;
    }

    /**
     * @param commercialInsurance
     */
    public void setCommercialInsurance(String commercialInsurance) {
        this.commercialInsurance = commercialInsurance;
    }

}