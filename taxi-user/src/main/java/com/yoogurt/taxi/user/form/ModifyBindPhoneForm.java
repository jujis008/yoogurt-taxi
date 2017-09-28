package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ModifyBindPhoneForm {
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String phoneCode;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$",message = "手机号格式有误")
    private String phoneNumber;
}
