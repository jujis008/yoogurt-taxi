package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class AuthorityForm {
    private Long id;
    @NotBlank(message = "权限名不能为空")
    @Length(max = 20,message = "权限名不能超过20个字符")
    private String authorityName;
    @NotBlank(message = "权限组不能为空")
    @Length(max = 32,message = "权限组名不能超过32个字符")
    private String authorityGroup;
    @NotBlank(message = "接口名不能为空")
    @Length(max = 32,message = "接口名不能超过32个字符")
    private String uri;

    private String associatedControl;
    private String remark;
}
