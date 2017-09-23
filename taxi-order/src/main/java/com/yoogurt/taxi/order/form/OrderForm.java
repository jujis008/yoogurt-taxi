package com.yoogurt.taxi.order.form;

import javax.validation.constraints.NotNull;

public abstract class OrderForm {

    @NotNull(message = "请指定租单")
    private Long orderId;
}
