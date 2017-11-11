package com.yoogurt.taxi.dal.condition.account;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class RecordCondition {
    private String userId;
    private BigDecimal amount;
    private String transactionNo;
    private Integer type;
    private String remark;
}
