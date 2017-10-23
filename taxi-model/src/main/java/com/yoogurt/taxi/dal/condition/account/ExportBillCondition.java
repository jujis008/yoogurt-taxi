package com.yoogurt.taxi.dal.condition.account;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ExportBillCondition {
    private String phoneNumber;
    private Integer billType;
    private Integer billStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    private Integer tradeType;
    private Integer destinationType;
}
