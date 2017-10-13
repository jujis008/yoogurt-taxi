package com.yoogurt.taxi.dal.model.user;

import com.yoogurt.taxi.dal.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverWLModel {
    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String idCard;
    private String plateNumber;
    private Integer status;
    private String statusName;

    public void setStatus(Integer status) {
        this.status = status;
        UserStatus userStatus = UserStatus.getEnumsByCode(status);
        if (userStatus != null) {
            this.statusName = userStatus.getName();
        }
    }
}
