package com.yoogurt.taxi.dal.enums;

public enum BillStatus {
    /**
     *待处理
     */
    PENDING(10,"待处理"),
    /**
     *转账中
     */
    TRANSFERRING(20,"转账中"),
    /**
     *成功
     */
    SUCCESS(30,"成功"),
    /**
     *失败
     */
    FAIL(40,"失败"),
    /**
     *拒绝
     */
    REFUSE(50,"拒绝"),
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
