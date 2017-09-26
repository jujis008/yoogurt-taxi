package com.yoogurt.taxi.order.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RentForm {

    private Long userId;

    @NotBlank(message = "请指定交班地址")
    private String address;

    @NotNull(message = "请指定交班地点经纬度")
    private Double lng;

    @NotNull(message = "请指定交班地点经纬度")
    private Double lat;

    @NotNull(message = "请指定交班时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handoverTime;

    @NotNull(message = "请指定还车时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date giveBackTime;

    @NotNull(message = "请填写出租价格")
    private BigDecimal price;
}
