package com.yoogurt.taxi.dal.condition.account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class RecordCondition {
    private Long userId;
    private BigDecimal amount;
    private Long transactionNo;
    private Integer type;
    private String remark;
}
