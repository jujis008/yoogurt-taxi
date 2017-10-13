package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "finance_bill")
@Getter
@Setter
public class FinanceBill extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 账单编号，与外部系统对接，使用此字段标识
     */
    @Column(name = "bill_no")
    private Long billNo;

    /**
     * 交易流水号
     */
    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "context_id")
    private Long contextId;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    private String name;

    private String username;

    /**
     * 用户类型，参考user表
     */
    @Column(name = "user_type")
    private Integer userType;

    /**
     * 账单状态：10-待处理，20-处理中，30-处理成功，40-失败，50拒绝
     */
    private Integer status;

    /**
     * 账单类型：10-充值，20-提现，30-赔偿，40-补偿，50-订单收入
     */
    private Integer type;

    /**
     * 资金去向：1-押金，2-余额，3-支付宝，4-微信，5-银行，6-其它
     */
    @Column(name = "destination_type")
    private Integer destinationType;

    /**
     * 收款账号
     */
    @Column(name = "payee_account")
    private String payeeAccount;

    /**
     * 收款人姓名
     */
    @Column(name = "payee_name")
    private String payeeName;

    /**
     * 收款人手机号
     */
    @Column(name = "payee_phone")
    private String payeePhone;

    /**
     * 付款方式：1-押金，2-余额，3-支付宝，4-微信，5-银行卡，6-其它
     */
    private Integer payment;

    /**
     * 付款人账号
     */
    @Column(name = "drawee_account")
    private String draweeAccount;

    /**
     * 付款人姓名
     */
    @Column(name = "drawee_name")
    private String draweeName;

    @Column(name = "drawee_phone")
    private String draweePhone;

    /**
     * 交易描述
     */
    private String description;

}