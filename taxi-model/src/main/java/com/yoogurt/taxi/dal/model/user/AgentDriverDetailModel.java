package com.yoogurt.taxi.dal.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.UserGender;
import com.yoogurt.taxi.dal.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AgentDriverDetailModel {
    private String userId;
    private String driverId;
    private String name;
    private Integer userStatus;
    private String userStatusName;
    private String idFront;
    private String idBack;
    private String phoneNumber;
    private Integer gender;
    private String genderName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    private String houseHold;
    private String address;
    private String drivingLicense;
    private String drivingLicenseFront;
    private String drivingLicenseBack;
    private String serviceNumber;
    private String servicePicture;

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
        this.userStatusName = UserStatus.getEnumsByCode(userStatus).getName();
    }

    public void setGender(Integer gender) {
        this.gender = gender;
        this.genderName = UserGender.getEnumsByCode(gender).getName();
    }
}
