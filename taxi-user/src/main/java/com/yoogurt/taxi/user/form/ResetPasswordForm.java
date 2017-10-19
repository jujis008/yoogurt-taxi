package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class ResetPasswordForm {
    @NotBlank(message = "请输入验证码")
    @Length(min = 6,max = 6,message = "验证码为6位数字")
    private String phoneCode;
    @NotBlank(message = "请输入新的密码")
    private String password;
}
