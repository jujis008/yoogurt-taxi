package com.yoogurt.taxi.dal.model.user;

import com.yoogurt.taxi.dal.beans.CarInfo;
import com.yoogurt.taxi.dal.enums.CarEnergyType;

public class CarInfoEnumModel  extends CarInfo{
    private String energyTypeName;

    public String getEnergyTypeName() {
        return CarEnergyType.getEnumsByCode(super.getEnergyType()).getName();
    }
}
