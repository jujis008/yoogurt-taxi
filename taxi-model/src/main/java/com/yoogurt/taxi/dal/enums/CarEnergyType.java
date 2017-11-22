package com.yoogurt.taxi.dal.enums;

public enum CarEnergyType {
    /**
     * 汽油
     */
    GASOLINE(10,"汽油"),
    /**
     * 柴油
     */
    DIESEL(20,"柴油"),
    /**
     * 油电混合
     */
    OIL_MIX_ELECTRIC(30,"油电混合"),
    /**
     * 纯电动
     */
    ELECTRIC(40,"纯电动"),
    /**
     * 插电式混动
     */
    PLUG_IN_HYBRID(50,"插电式混动"),
    ;

    private Integer code;
    private String name;

    CarEnergyType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CarEnergyType getEnumsByCode(Integer code) {
        for (CarEnergyType type:CarEnergyType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
