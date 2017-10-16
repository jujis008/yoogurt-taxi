package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "finance_account")
@Domain
@Getter
@Setter
public class FinanceAccount extends SuperModel{
    @Id
    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String username;

    @Column(name = "user_type")
    private Integer userType;

    /**
     * 账号
     */
    @Column(name = "account_no")
    private Long accountNo;

    /**
     * 余额
     */
    private BigDecimal balance;

    @Column(name = "frozen_balance")
    private BigDecimal frozenBalance;

    /**
     * 应收押金
     */
    @Column(name = "receivable_deposit")
    private BigDecimal receivableDeposit;

    /**
     * 已收押金
     */
    @Column(name = "received_deposit")
    private BigDecimal receivedDeposit;

    @Column(name = "frozen_deposit")
    private BigDecimal frozenDeposit;

}