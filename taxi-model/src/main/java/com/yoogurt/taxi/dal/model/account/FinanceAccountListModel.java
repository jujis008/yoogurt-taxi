package com.yoogurt.taxi.dal.model.account;

import com.yoogurt.taxi.dal.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FinanceAccountListModel {
    private String userId;
    private String name;
    private String username;
    private Integer userType;
    private String userTypeName;
    private BigDecimal balance;
    private BigDecimal frozenBalance;
    private BigDecimal receivableDeposit;
    private BigDecimal receivedDeposit;
    private BigDecimal frozenDeposit;

    public void setUserType(Integer userType) {
        this.userType = userType;
        this.userTypeName = UserType.getEnumsByCode(userType).getName();
    }
}
