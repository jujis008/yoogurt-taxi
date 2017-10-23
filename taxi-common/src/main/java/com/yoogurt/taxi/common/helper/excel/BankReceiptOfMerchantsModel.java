package com.yoogurt.taxi.common.helper.excel;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BankReceiptOfMerchantsModel {
    private String accountNo;
    private String accountName;
    private BigDecimal amount;
    private Boolean status;
    private String description;
    private String note;
    private String bankName;
    private String bankAddress;
}
