package com.yoogurt.taxi.dal.condition.account;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.enums.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateCondition {
    /**账户所有者（必穿）*/
    private String userId;
    /**变动金额（必穿）*/
    private Money money;
    /**交易类型（必穿）*/
    private TradeType tradeType;
    /**目的账户类型（必传）*/
    private DestinationType destinationType;
    /**支付方式（必传）*/
    private Payment payment;
    /**存入账号（必传）*/
    private String payeeAccount;
    /**收款人姓名（必传）*/
    private String payeeName;
    /**收款人手机号（必传）*/
    private String payeePhone;

    private String bankName;
    private String bankAddress;
    /**付款人账号（必传）*/
    private String draweeAccount;
    /**付款人姓名（必传）*/
    private String draweeName;
    /**付款人手机号（必传）*/
    private String draweePhone;
    /**账单id(回调必传)*/
    private Long billId;
    /**交易流水号（充值必传）*/
    private String transactionNo;
    /**交易对象id（罚款，补偿，订单收入）*/
    private String contextId;

    /** 操作行为：1-冻结，2-冻结扣除，3-冻结返回*/
    private AccountChangeType changeType;
    private String remark;
}
