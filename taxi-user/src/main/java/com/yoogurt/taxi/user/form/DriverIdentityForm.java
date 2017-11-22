package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;


@Getter
@Setter
public class DriverIdentityForm {
    @Pattern(regexp = "10|20",message = "未指定性别")
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthday;
    @NotBlank(message = "户籍不能为空")
    private String household;
    @NotBlank(message = "请上传身份证图片")
    private String idFront;
    @NotBlank(message = "请上传身份证图片")
    private String idBack;
    @NotBlank(message = "请上传现居住地")
    private String address;
    @NotBlank(message = "请上传驾驶证图片")
    private String drivingLicenseFront;
    @NotBlank(message = "请上传驾驶证图片")
    private String drivingLicenseBack;
    @NotBlank(message = "请上传服务资格证图片")
    private String servicePicture;

}
