package com.yoogurt.taxi.dal.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.TradeType;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class FinanceBillListWebModel {
    private Long id;
    private String name;
    private String username;
    private Integer userType;
    private String userTypeName;
    private Integer tradeType;
    private String tradeTypeName;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date gmtCreate;

    public void setUserType(Integer userType) {
        this.userType = userType;
        this.userTypeName = UserType.getEnumsByCode(userType).getName();
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
        this.tradeTypeName = TradeType.getEnumsBycode(tradeType).getName();
    }
}
