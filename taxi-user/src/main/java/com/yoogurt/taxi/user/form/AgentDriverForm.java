package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Setter
public class AgentDriverForm {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$",message = "手机号格式有误")
    private String username;

    @NotNull(message = "司机id不能为空")
    private Long driverId;
//    @NotBlank(message = "手机号不能为空")
//    @Pattern(regexp = "(\\+\\d+)?1[34578]\\d{9}$",message = "手机号格式有误")
//    private String mobile;
    @NotNull(message = "请选择性别")
    private Integer gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "[1-9]\\d{13,16}(\\d|[X|x]{1})",message = "身份证号格式有误")
    private String idCard;
    @NotBlank(message = "驾驶证号不能为空")
    private String drivingLicense;
    @NotBlank(message = "户籍所在地不能为空")
    private String household;
    @NotBlank(message = "现居住地不能为空")
    private String address;
    @NotBlank(message = "请上传身份证正面照")
    private String idFront;
    @NotBlank(message = "请上传身份证背面照")
    private String idBack;
    @NotBlank(message = "服务资格证号不能为空")
    private String serviceNumber;
    @NotBlank(message = "服务资格证图片不能为空")
    private String servicePicture;
    @NotBlank(message = "驾驶证正面照不能为空")
    private String drivingLicenseFront;
    @NotBlank(message = "驾驶证背面照不能为空")
    private String drivingLicenseBack;
}
