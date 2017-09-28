package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class ModifyPasswordForm {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
