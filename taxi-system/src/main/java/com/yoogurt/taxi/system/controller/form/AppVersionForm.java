package com.yoogurt.taxi.system.controller.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AppVersionForm {
    private Long id;
    @NotNull(message = "请指明系统类型")
    private Integer sysType;
    @NotNull(message = "请指明应用类型")
    private Integer appType;
    @NotBlank(message = "版本名称不能为空")
    private String versionName;
    @NotNull(message = "版本号不能为空")
    private Integer versionNumber;
    @NotBlank(message = "下载地址不能为空")
    private String downloadUrl;
    @NotNull(message = "是否强制更新")
    private Boolean isForce;
    private String description;
}
