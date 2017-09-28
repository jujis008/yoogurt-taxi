package com.yoogurt.taxi.user.form;

import org.hibernate.validator.constraints.NotBlank;

public class AuthorityForm {
    private Long id;
    @NotBlank(message = "权限名不能为空")
    private String authorityName;
    @NotBlank(message = "权限组不能为空")
    private String authorityGroup;
    @NotBlank(message = "接口名不能为空")
    private String uri;

    private String associatedControl;
    private String remark;
}
