package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "finance_bank_card")
@Getter
@Setter
public class FinanceBankCard extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 卡号
     */
    @Column(name = "card_no")
    private String cardNo;

    /**
     * 开户行
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 预留姓名
     */
    @Column(name = "reserved_name")
    private String reservedName;

    /**
     * 预留手机号
     */
    @Column(name = "reserved_phone")
    private String reservedPhone;

    /**
     * 是否首选
     */
    @Column(name = "is_primary")
    private Boolean isPrimary;
}