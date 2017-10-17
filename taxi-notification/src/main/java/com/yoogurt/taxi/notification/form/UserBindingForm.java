package com.yoogurt.taxi.notification.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class UserBindingForm {

    @NotBlank(message = "请指定设备")
    private String clientId;

    private Long userId;
}
