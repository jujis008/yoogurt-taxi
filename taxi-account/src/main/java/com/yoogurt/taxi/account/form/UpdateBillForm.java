package com.yoogurt.taxi.account.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateBillForm {
    @NotNull(message = "操作对象id不能为空")
    private Long id;
    @NotNull(message = "操作标识不能为空")
    private Integer status;

    private String remark;
}
