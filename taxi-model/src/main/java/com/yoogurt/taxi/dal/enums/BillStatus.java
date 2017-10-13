package com.yoogurt.taxi.dal.enums;

public enum BillStatus {
    PENDING(10,"待处理"),
    TRANSFERRING(20,"转账中"),
    SUCCESS(30,"成功"),
    REFUSE(40,"失败"),
    FAIL(50,"拒绝"),
    ;

    private Integer code;

    private String name;

    public static BillStatus getEnumsByCode(Integer code) {
        for (BillStatus enums: BillStatus.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }

    BillStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
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
