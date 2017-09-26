package com.yoogurt.taxi.user.Form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class ModifyBindPhoneForm {
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String phoneCode;
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;
}
