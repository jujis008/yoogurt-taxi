package com.yoogurt.taxi.dal.condition.account;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class ExportBillCondition {
    private String phoneNumber;
    private Integer billType;
    private Integer billStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String endTime;
    private Integer tradeType;
    private Integer destinationType;
}
