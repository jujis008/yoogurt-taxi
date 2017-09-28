package com.yoogurt.taxi.user.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserAddressForm {
    private Long id;
    @NotNull(message = "经度不能为空")
    private Double lng;
    @NotNull(message = "纬度不能为空")
    private Double lat;

    @NotBlank(message = "地区不能为空")
    private String area;
    /**
     * 国标地区编码
     */
    @NotBlank(message = "行政区划代码不能为空")
    private String adcode;


    private String address;
    @NotNull(message = "首选参数错误")
    private Boolean isPrimary;
}
