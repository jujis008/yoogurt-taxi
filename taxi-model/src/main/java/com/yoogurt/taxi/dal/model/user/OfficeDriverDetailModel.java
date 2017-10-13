package com.yoogurt.taxi.dal.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.CarEnergyType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OfficeDriverDetailModel extends AgentDriverDetailModel{
    private Long id;
    private String plateNumber;
    private String carPicture;
    private String vehicleType;
    private Integer energyType;
    private String energyTypeName;
    private String engineNumber;
    private String vin;
    private String company;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date vehicleRegisterTime;
    private String drivingPermitFront;
    private String drivingPermitBack;
    private String compulsoryInsurance;
    private String commercialInsurance;

    public void setEnergyType(Integer energyType) {
        this.energyType = energyType;
        CarEnergyType carEnergyType = CarEnergyType.getEnumsByCode(energyType);
        if (carEnergyType != null) {
            this.energyTypeName = carEnergyType.getName();
        }
    }
}
