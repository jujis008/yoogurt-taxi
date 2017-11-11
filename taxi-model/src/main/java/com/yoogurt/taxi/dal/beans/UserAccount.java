package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user_account")
@Setter
@Getter
@Domain
public class UserAccount extends SuperModel{
    @Id
    @Column(name = "user_id")
    private String userId;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额
     */
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

    /**
     * 冻结押金
     */
    @Column(name = "frozen_deposit")
    private BigDecimal frozenDeposit;

}