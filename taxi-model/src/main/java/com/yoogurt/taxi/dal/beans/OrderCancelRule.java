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
@Table(name = "order_cancel_rule")
public class OrderCancelRule extends SuperModel{
    @Id
    @Column(name = "rule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    /**
     * 36~48小时，start=36，end=48；24小时以下，start=0，end=24
     */
    private Integer start;

    /**
     * 36~48小时，start=36，end=48；24小时以下，start=0，end=24
     */
    private Integer end;

    /**
     * 时长单位：SECONDS-秒，MINUTES-分，HOURS-小时
     */
    private String unit;

    /**
     * 0-否，1-是
     */
    @Column(name = "allow_cancel")
    private Boolean allowCancel;

    /**
     * 扣除违约金的百分比，存放小数，不带%
     */
    private BigDecimal percent;

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

}