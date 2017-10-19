package com.yoogurt.taxi.system.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FeedbackForm {
    @NotBlank(message = "请具体描述")
    private String content;
    @NotNull(message = "请选择反馈类型")
    private Long feedbackType;
    @NotBlank(message = "手机型号不能为空")
    private String phoneModel;
    @NotBlank(message = "系统版本号不能为空")
    private String systemVersion;
    @NotBlank(message = "应用版本号不能为空")
    private String appVersion;
}
