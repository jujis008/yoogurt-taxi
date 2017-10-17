package com.yoogurt.taxi.dal.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class WithdrawBillListWebModel {
    private Long id;
    private String username;
    private Integer billType;
    private String billTypeName;
    private String payeeAccount;
    private BigDecimal amount;
    private Integer billStatus;
    private String billStatusName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date gmtCreate;

    public void setBillType(Integer billType) {
        this.billType = billType;
        this.billTypeName = BillType.getEnumsByCode(billType).getName();
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
        this.billStatusName = BillStatus.getEnumsByCode(billStatus).getName();
    }
}
