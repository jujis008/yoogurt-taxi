package com.yoogurt.taxi.dal.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WithdrawBillDetailModel {
    private Long id;
    private String name;
    private String username;
    private Integer billType;
    private String billTypeName;
    private BigDecimal amount;
    private String payeeName;
    private String payeeAccount;
    private String bankName;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date gmtCreate;
    private Integer billStatus;
    private String billStatusName;

    public void setBillType(Integer billType) {
        this.billType = billType;
        this.billTypeName = BillType.getEnumsByCode(billType).getName();
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
        this.billStatusName = BillStatus.getEnumsByCode(billStatus).getName();
    }
}
