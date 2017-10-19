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
}
