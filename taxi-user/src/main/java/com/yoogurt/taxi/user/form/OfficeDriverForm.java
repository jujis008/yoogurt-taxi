package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class OfficeDriverForm extends AgentDriverForm{
    @NotNull(message = "车辆id不能为空")
    private Long carId;
    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;
    @NotBlank(message = "车身照不能为空")
    private String carPicture;
    @NotBlank(message = "车型不能为空")
    private String vehicleType;
    @NotNull(message = "能源类型不能为空")
    private Integer energyType;
    @NotBlank(message = "发动机号不能为空")
    private String engineNumber;
    @NotBlank(message = "车架号不能为空")
    private String vin;
    @NotBlank(message = "所属公司不能为空")
    private String company;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "车辆注册时间不能为空")
    private Date vehicleRegisterTime;
    @NotBlank(message = "行车证正面照不能为空")
    private String drivingPermitFront;
    @NotBlank(message = "行车证背面照不能为空")
    private String drivingPermitBack;
    @NotNull(message = "交强险照片不能为空")
    private String compulsoryInsurance;
    @NotNull(message = "商业险照片不能为空")
    private String commercialInsurance;
}
