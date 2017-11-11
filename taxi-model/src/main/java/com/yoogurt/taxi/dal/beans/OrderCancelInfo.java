package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_cancel_info")
public class OrderCancelInfo extends SuperModel {
    /**
     * 订单号
     */
    @Id
    @Column(name = "order_id")
    private String orderId;

    /**
     * 如果没有违约，规则id为0
     */
    @Column(name = "rule_id")
    private Long ruleId;

    /**
     * 10-车主，20-司机，30-无责
     */
    @Column(name = "responsible_party")
    private Integer responsibleParty;

    /**
     * 取消违约罚款
     */
    @Column(name = "fine_money")
    private BigDecimal fineMoney;

    /**
     * 取消原因
     */
    private String reason;

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

    /**
     * 0-否，1-是
     */
    @Column(name = "is_disobey")
    private Boolean isDisobey;

    /**
     * 指的是取消时间到交车时间的间隔，按照向上取整的原则计算
     */
    private Integer time;

    /**
     * 时长单位：SECONDS-秒，MINUTES-分，HOURS-小时
     */
    private String unit;

}