package com.yoogurt.taxi.account.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawForm {
    @NotBlank(message = "请输入交易密码")
    private String payPassword;
    @NotNull(message = "请指明提现类型")
    private Integer payment;
    @NotBlank(message = "请输入预留姓名")
    private String reservedName;
    @NotBlank(message = "请输入账号")
    private String accountNo;
    @NotBlank(message = "请输入开户行")
    private String bankName;
    @NotBlank(message = "请输入开户地")
    private String bankAddress;
    @NotNull(message = "请输入提现金额")
    @Min(value = 1)
    @Max(value = 15000)
    private BigDecimal withdrawMoney;
    @NotNull(message = "请指明提现目的账户类型")
    private Integer destinationType;
}
