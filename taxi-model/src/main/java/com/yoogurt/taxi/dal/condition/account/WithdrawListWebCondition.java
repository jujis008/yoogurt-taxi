package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class WithdrawListWebCondition extends PageableCondition{
    private String username;
    private Integer billType;
    private Integer billStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    private Integer tradeType;
}
