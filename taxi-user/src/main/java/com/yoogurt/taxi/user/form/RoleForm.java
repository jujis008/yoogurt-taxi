package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class RoleForm {
    private Long id;
    @NotBlank(message = "请输入角色名")
    private String roleName;
    @NotBlank(message = "请输入角色代码")
    private String roleCode;
}
