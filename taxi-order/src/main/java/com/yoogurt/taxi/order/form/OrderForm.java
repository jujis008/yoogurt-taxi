package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public abstract class OrderForm {

    @NotNull(message = "请指定租单")
    private Long orderId;

    private Long userId;
}
