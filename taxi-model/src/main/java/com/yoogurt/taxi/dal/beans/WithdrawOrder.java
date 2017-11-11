package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "withdraw_order")
@Setter
@Getter
@Domain
public class WithdrawOrder extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 提现人
     */
    @Column(name = "user_id")
    private String userId;

    private String username;

    /**
     * 充值/提现金额
     */
    private BigDecimal amount;

    /**
     * 工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     */
    private Integer status;

    /**
     * 收款账号
     */
    @Column(name = "destination_account")
    private String destinationAccount;

    /**
     * 预留姓名
     */
    @Column(name = "reserved_name")
    private String reservedName;

    /**
     * 开户行
     */
    @Column(name = "bank_of_deposit")
    private String bankOfDeposit;

}