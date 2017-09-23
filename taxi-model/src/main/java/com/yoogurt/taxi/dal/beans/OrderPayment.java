package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_payment")
public class OrderPayment extends SuperModel{
    @Id
    @Column(name = "pay_id")
    private String payId;

    /**
     * 订单号
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * wallet-钱包支付，
            alipay-支付宝 APP 支付，
            alipay_wap-支付宝手机网页支付，
            alipay_pc_direct-支付宝电脑网站支付，
            alipay_qr-支付宝当面付，即支付宝扫码支付，
            upacp-银联 APP 支付，
            upacp_wap-银联手机网页支付，
            upacp_pc-即银联 PC 网页支付，
            wx-微信App支付
     */
    @Column(name = "pay_channel")
    private String payChannel;

    private BigDecimal amount;

    private String subject;

    private String body;

    @Column(name = "paid_time")
    private Date paidTime;

    @Column(name = "transaction_no")
    private String transactionNo;

    /**
     * 10-支付未完成，20-支付已完成
     */
    private Integer status;

}