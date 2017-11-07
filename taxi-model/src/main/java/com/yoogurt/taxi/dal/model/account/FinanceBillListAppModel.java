package com.yoogurt.taxi.dal.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class FinanceBillListAppModel {
    private Long id;
    private Long billNo;
    private Long contextId;
    private BigDecimal amount;
    private Integer tradeType;
    private String tradeTypeName;
    private Integer billStatus;
    private String billStatusName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date gmtCreate;

    public void setTradeType(Integer type) {
        this.tradeType = type;
        this.tradeTypeName = TradeType.getEnumsByCode(type).getName();
    }

    public void setBillStatus(Integer status) {
        this.billStatus = status;
        this.billStatusName = BillStatus.getEnumsByCode(status).getName();
    }
}
