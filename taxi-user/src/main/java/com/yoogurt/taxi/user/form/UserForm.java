package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserForm {
    private Long userId;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$",message = "手机号格式有误")
    private String username;
    @NotNull(message = "请选择角色")
    private Long roleId;
    @NotNull(message = "请输入工号")
    private String employeeNo;
    @NotNull(message = "请输入姓名")
    private String name;
    @NotNull(message = "请输入密码")
    private String loginPassword;

}
