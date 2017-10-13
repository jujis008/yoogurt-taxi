package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class RoleAuthorityForm {
    @NotNull(message = "角色id不能为空")
    private Long roleId;
    @NotEmpty(message = "权限列表不能为空")
    private List<Long> authorityList;
}
