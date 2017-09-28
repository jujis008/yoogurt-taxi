package com.yoogurt.taxi.user.Form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CarIdentityForm {
    @Pattern(regexp = "10|20|30|40|50",message = "不支持的能源类型")
    private String energyType;
    @NotBlank(message = "发动机号不能为空")
    private String engineNumber;
    @Length(max = 6,min = 6,message = "车架后长度为6位")
    @NotBlank(message = "请填写车架号")
    private String vin;
    @NotBlank(message = "请上传车前照")
    private String carPicture;
    @NotBlank(message = "请上传行车证正面照")
    private String drivingPermitFront;
    @NotBlank(message = "请上传行车证反面照")
    private String drivingPermitBack;
    @NotBlank(message = "请上传交强险")
    private String compulsoryInsurance;
    @NotBlank(message = "请上传商业险")
    private String commercialInsurance;
}
