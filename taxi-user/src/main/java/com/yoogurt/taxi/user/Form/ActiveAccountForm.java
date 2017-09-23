package com.yoogurt.taxi.user.Form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ActiveAccountForm {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$",message = "手机号格式有误")
    private String phoneNumber;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "[1-9]\\d{13,16}[X|x]{1}",message = "身份证号格式有误")
    private String idCard;
}
