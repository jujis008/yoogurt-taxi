package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public abstract class BaseOrderForm {

    @NotNull(message = "请指定租单")
    private String orderId;

    private String userId;
}
