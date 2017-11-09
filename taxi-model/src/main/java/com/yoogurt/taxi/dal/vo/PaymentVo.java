package com.yoogurt.taxi.dal.vo;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class PaymentVo {

    @NotBlank(message = "请指定支付对象")
    private String payId;

    @NotNull(message = "请指定支付时间")
    private Long paidTime;

    @NotNull(message = "请指定支付金额")
    private Long paidAmount;

    @NotNull(message = "请指定交易流水号")
    private String transactionNo;

}
