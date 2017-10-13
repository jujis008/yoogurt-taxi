package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateCondition {
    /**账户所有者*/
    private Long userId;
    /**变动金额*/
    private Money money;
    /**账单类型*/
    private BillType billType;
    /**目的账户类型*/
    private DestinationType destinationType;
    /**支付方式（资金来源账户类型）*/
    private Payment payment;
    /**存入账号（提现时需求参数）*/
    private String payeeAccount;

    private String payeeName;

    private String payeePhone;

    private String draweeAccount;

    private String draweeName;

    private String draweePhone;

    private Long billId;

    private String transactionNo;

    private Long contextId;
}
